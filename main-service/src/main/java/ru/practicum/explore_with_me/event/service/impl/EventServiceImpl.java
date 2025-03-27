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
import ru.practicum.explore_with_me.error.model.AlreadyConfirmedException;
import ru.practicum.explore_with_me.error.model.AlreadyPublishedException;
import ru.practicum.explore_with_me.error.model.GetPublicEventException;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.error.model.PublicationException;
import ru.practicum.explore_with_me.error.model.TooManyRequestsException;
import ru.practicum.explore_with_me.error.model.UpdateStartDateException;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import ru.practicum.explore_with_me.event.dto.AdminPatchEventDto;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventUserRequest;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.Location;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.event.model.enums.EventStateAction;
import ru.practicum.explore_with_me.event.model.enums.SortType;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.event.utils.EventFinder;
import ru.practicum.explore_with_me.event.utils.LocationFinder;
import ru.practicum.explore_with_me.event.utils.specification.EventFindSpecification;
import ru.practicum.explore_with_me.request.dao.RequestRepository;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.model.Request;
import ru.practicum.explore_with_me.request.model.enums.RequestStatus;
import ru.practicum.explore_with_me.user.utils.UserFinder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final EventMapper eventMapper;
    final RequestMapper requestMapper;
    final EventFinder eventFinder;
    final LocationFinder locationFinder;
    final UserFinder userFinder;
    final EntityManager entityManager;

    @Override
    public Collection<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        log.info("Get events with {userId, from, size} = ({}, {}, {})", userId, from, size);
        return page.getContent().stream().map(eventMapper::toShortDto).toList();
    }

    @Override
    public Collection<EventFullDto> getAllEventsAdmin(Set<Long> users, Set<String> states, Set<Long> categories,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                                      Integer size) {
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

        return page.stream()
                .peek(event -> event.setConfirmedRequests(
                        requestRepository.findAllByEventIdAndStatus(event.getId(),RequestStatus.CONFIRMED).size())
                )
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Override
    public Collection<EventShortDto> getAllEventsPublic(String text, Set<Long> categories, Boolean paid,
                                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                        Boolean onlyAvailable, SortType sort, Integer from,
                                                        Integer size, HttpServletRequest httpServletRequest) {
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
        Event event = eventFinder.findById(eventId);

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
            Location location = locationFinder.findById(adminPatchEventDto.getLocation().getLat(),
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
        Location location = locationFinder.findById(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());

        event.setInitiator(userFinder.getUserById(userId));
        event.setLocation(location);

        log.info("Create new event with userId = {}", userId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        return eventMapper.toFullDto(eventFinder.findByIdAndInitiatorId(userId, eventId));
    }

    @Override
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventFinder.findById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new GetPublicEventException("Event must be published");
        }

        saveViewInStatistic("/events/" + eventId, httpServletRequest.getRemoteAddr());
        List<GetResponse> getResponses = loadViewInStatistic(event.getPublishedOn(), LocalDateTime.now(), List.of("/events/" + eventId), true);
        if (!getResponses.isEmpty()) {
            GetResponse getResponse = getResponses.getFirst();
            event.setViews(getResponse.getHits());
        }
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = eventFinder.findByIdAndInitiatorId(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new AlreadyPublishedException("Event with eventId = " + eventId + "has already been published");
        }

        stateChanger(event, updateRequest.getStateAction());
        if (updateRequest.getLocation() != null) {
            Location location = locationFinder.findById(updateRequest.getLocation().getLat(),
                    updateRequest.getLocation().getLon());
            event.setLocation(location);
        }

        eventMapper.updateUserRequest(updateRequest, event);
        log.info("Update event with eventId = {}", eventId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public Collection<RequestDto> getRequests(Long userId, Long eventId) {
        userFinder.getUserById(userId);
        Event event = eventFinder.findById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("The event initiator does not match the user id");
        }

        Set<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream().map(requestMapper::toRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest) {
        userFinder.getUserById(userId);
        Event event = eventFinder.findById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("The event initiator does not match the user id");
        }

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
        StatsClient.hit(hitRequest);
    }

    private List<GetResponse> loadViewInStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return StatsClient.getStats(start, end, uris, unique);
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
}