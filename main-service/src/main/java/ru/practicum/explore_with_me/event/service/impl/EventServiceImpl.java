package ru.practicum.explore_with_me.event.service.impl;

import dto.GetResponse;
import dto.HitRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.explore_with_me.error.model.*;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import ru.practicum.explore_with_me.event.dao.LocationRepository;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.Location;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.event.model.enums.EventStateAction;
import ru.practicum.explore_with_me.event.model.enums.SortType;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.event.utils.specification.EventFindSpecification;
import ru.practicum.explore_with_me.request.dao.RequestRepository;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.model.Request;
import ru.practicum.explore_with_me.request.model.enums.RequestStatus;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final EventMapper eventMapper;
    final RequestMapper requestMapper;
    final LocationRepository locationRepository;
    final UserRepository userRepository;
    final EntityManager entityManager;
    final StatsClient statsClient;

    @Override
    public Collection<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        log.info("Get events with {userId, from, size} = ({}, {}, {})", userId, from, size);
        return page.getContent().stream().map(eventMapper::toShortDto).toList();
    }

    @Override
    public Collection<EventFullDto> getAllEventsAdmin(GetAllEventsAdminParams params) {
        Set<Long> users = params.getUsers();
        Set<String> states = params.getStates();
        Set<Long> categories = params.getCategories();
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        Integer from = params.getFrom();
        Integer size = params.getSize();

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root);

        Specification<Event> specification = Specification
                .where(EventFindSpecification.userIn(users))
                .and(EventFindSpecification.stateIn(states))
                .and(EventFindSpecification.categoryIn(categories))
                .and(EventFindSpecification.eventDateAfter(rangeStart))
                .and(EventFindSpecification.eventDateBefore(rangeEnd));
        Page<Event> page = eventRepository.findAll(specification, pageable);

        log.info("Get events with {users, states, categories, rangeStart, rangeEnd, from, size} = ({},{},{},{},{},{},{})",
                users, size, categories, rangeStart, rangeEnd, from, size);

        List<Request> confirmedRequestsByEventId = requestRepository.findAllByEventIdInAndStatus(
                page.stream().map(Event::getId).toList(), RequestStatus.CONFIRMED);

        Map<Long, List<Request>> eventIdToConfirmedRequests = confirmedRequestsByEventId.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()));

        return page.stream()
                .map(event -> {
                   event.setConfirmedRequests(eventIdToConfirmedRequests.getOrDefault(
                           event.getId(),
                           Collections.emptyList()).size());
                   return eventMapper.toFullDto(event);
                })
                .toList();
    }

    @Override
    public Collection<EventShortDto> getAllEventsPublic(GetAllEventsPublicParams params) {
        String text = params.getText();
        Set<Long> categories = params.getCategories();
        Boolean paid = params.getPaid();
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        Boolean onlyAvailable = params.getOnlyAvailable();
        SortType sort = params.getSort();
        Integer from = params.getFrom();
        Integer size = params.getSize();
        HttpServletRequest httpServletRequest = params.getHttpServletRequest();

        Pageable pageable = PageRequest.of(from / size, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Time period incorrect");
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root);

        Specification<Event> specification = Specification
                .where(EventFindSpecification.textInAnnotationOrDescription(text))
                .and(EventFindSpecification.categoryIn(categories))
                .and(EventFindSpecification.eventDateAfter(rangeStart))
                .and(EventFindSpecification.eventDateBefore(rangeEnd))
                .and(EventFindSpecification.isAvailable(onlyAvailable))
                .and(EventFindSpecification.sortBySortType(sort))
                .and(EventFindSpecification.onlyPublished());
        Page<Event> page = eventRepository.findAll(specification, pageable);

        saveViewInStatistic("/events", httpServletRequest.getRemoteAddr());

        log.info("Get events with {text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size} = ({},{},{},{},{},{},{},{},{})",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return page.stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto patchEventById(Long eventId, AdminPatchEventDto adminPatchEventDto) {
        Event event = findEventById(eventId);

        LocalDateTime updateStartDate = adminPatchEventDto.getEventDate();

        if (event.getState().equals(EventState.PUBLISHED) && LocalDateTime.now().isAfter(event.getPublishedOn().plusHours(1))) {
            throw new PublicationException("Change event no later than one hour before the start");
        }

        if (updateStartDate != null && updateStartDate.isBefore(LocalDateTime.now())) {
            throw new UpdateStartDateException("Date and time has already arrived");
        }

        EventStateAction updateStateAction = getUpdateStateAction(adminPatchEventDto, event);

        stateChanger(event, updateStateAction);

        if (adminPatchEventDto.getLocation() != null) {
            Location location = findLocationByLatAndLon(adminPatchEventDto.getLocation().getLat(),
                    adminPatchEventDto.getLocation().getLon());
            event.setLocation(location);
        }

        eventMapper.patchUserRequest(adminPatchEventDto, event);
        if (event.getState().equals(EventState.PUBLISHED)) {
            event.setPublishedOn(LocalDateTime.now());
        }

        log.info("Patch event with eventId = {}", eventId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Event event = eventMapper.toEvent(newEventDto);
        Location location = findLocationByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());

        User user = findUserById(userId);

        event.setInitiator(user);
        event.setLocation(location);

        log.info("Create new event with userId = {}", userId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        return eventMapper.toFullDto(findEventByIdAndInitiatorId(userId, eventId));
    }

    @Override
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = findEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new GetPublicEventException("Event must be published");
        }

        saveViewInStatistic("/events/" + eventId, httpServletRequest.getRemoteAddr());

        List<GetResponse> getResponses = loadViewFromStatistic(
                event.getPublishedOn(),
                LocalDateTime.now(),
                List.of("/events/" + eventId),
                true);
        if (!getResponses.isEmpty()) {
            GetResponse getResponse = getResponses.getFirst();
            event.setViews(getResponse.getHits());
        }
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = findEventByIdAndInitiatorId(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new AlreadyPublishedException("Event with eventId = " + eventId + "has already been published");
        }

        stateChanger(event, updateRequest.getStateAction());
        if (updateRequest.getLocation() != null) {
            Location location = findLocationByLatAndLon(updateRequest.getLocation().getLat(),
                    updateRequest.getLocation().getLon());
            event.setLocation(location);
        }

        eventMapper.updateUserRequest(updateRequest, event);
        log.info("Update event with eventId = {}", eventId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public Collection<RequestDto> getRequests(Long userId, Long eventId) {
        getVerifiedEvent(userId, eventId);

        Set<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream().map(requestMapper::toRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest) {
        Event event = getVerifiedEvent(userId, eventId);

        List<Request> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());

        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Request with requestId = " + request.getId() + "does not match eventId = " + eventId);
            }
        }

        int confirmedCount = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
        int size = updateRequest.getRequestIds().size();
        int confirmedSize = updateRequest.getStatus().equals(RequestStatus.CONFIRMED) ? size : 0;

        if (event.getParticipantLimit() != 0 && confirmedCount + confirmedSize > event.getParticipantLimit()) {
            throw new TooManyRequestsException("Event limit exceed");
        }

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        for (Request request : requests) {
            if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.toRequestDto(request));
            } else if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new AlreadyConfirmedException("The request cannot be rejected if it is confirmed");
                }
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toRequestDto(request));
            }
        }

        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private Event getVerifiedEvent(Long userId, Long eventId) {
        findUserById(userId);

        Event event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("The event initiator does not match the user id");
        }
        return event;
    }

    private void stateChanger(Event event, EventStateAction stateAction) {
        if (stateAction != null) {
            Map<EventStateAction, EventState> state = Map.of(
                    EventStateAction.SEND_TO_REVIEW, EventState.PENDING,
                    EventStateAction.CANCEL_REVIEW, EventState.CANCELED,
                    EventStateAction.PUBLISH_EVENT, EventState.PUBLISHED,
                    EventStateAction.REJECT_EVENT, EventState.CANCELED);
            event.setState(state.get(stateAction));
        }
    }

    private void saveViewInStatistic(String uri, String ip) {
        HitRequest hitRequest = HitRequest.builder()
                .app("ewm-main-service")
                .uri(uri)
                .ip(ip)
                .build();
        statsClient.hit(hitRequest);
    }

    private List<GetResponse> loadViewFromStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }

    private EventStateAction getUpdateStateAction(AdminPatchEventDto adminPatchEventDto, Event event) {
        EventStateAction updateStateAction = adminPatchEventDto.getStateAction();

        if (updateStateAction != null && !event.getState().equals(EventState.PENDING) && updateStateAction.equals(EventStateAction.PUBLISH_EVENT)) {
            throw new PublicationException("The event can only be published during the pending stage");
        }

        if (updateStateAction != null && updateStateAction.equals(EventStateAction.REJECT_EVENT)
                && event.getState().equals(EventState.PUBLISHED)) {
            throw new PublicationException("Cannot reject a published event");
        }
        return updateStateAction;
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %s, not found", eventId)));
    }

    private Event findEventByIdAndInitiatorId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d and userId=%d not found", eventId, userId)));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d + not found", userId)));
    }

    private Location findLocationByLatAndLon(Float lat, Float lon) {
        return locationRepository
                .findByLatAndLon(lat, lon)
                .orElseGet(() -> locationRepository.save(Location.builder().lat(lat).lon(lon).build()));
    }
}