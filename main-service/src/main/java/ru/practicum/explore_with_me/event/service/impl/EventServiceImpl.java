package ru.practicum.explore_with_me.event.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.error.model.AlreadyPublishedException;
import ru.practicum.explore_with_me.error.model.PublicationException;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.EventState;
import ru.practicum.explore_with_me.event.model.EventStateAction;
import ru.practicum.explore_with_me.event.model.Location;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.event.utils.EventFinder;
import ru.practicum.explore_with_me.event.utils.LocationFinder;
import ru.practicum.explore_with_me.event.utils.specification.EventFindSpecification;
import ru.practicum.explore_with_me.user.utils.UserFinder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventFinder eventFinder;
    private final LocationFinder locationFinder;
    private final UserFinder userFinder;
    private final EntityManager entityManager;

    @Override
    public Collection<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        log.info("Get events with {userId, from, size} = ({}, {}, {})", userId, from, size);
        return page.getContent().stream().map(eventMapper::toShortDto).toList();
    }

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

        if (page.isEmpty()) {
            return List.of();
        }

        log.info("Get events with {users, states, categories,rangeStart,rangeEnd,from,size} = ({}, {}, {},{},{},{},{})",
                users, size, categories, rangeStart, rangeEnd, from, size);
        return page.stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Override
    public EventFullDto patchEventById(Long eventId, AdminPatchEventDto adminPatchEventDto) {
        Event event = eventFinder.findById(eventId);

        if (event.getEventDate().isAfter(LocalDateTime.now().minusHours(1))) {
            throw new PublicationException("Change event no later than one hour before the start");
        }

        publishedValidation(eventId, event, adminPatchEventDto.getStateAction());

        if (adminPatchEventDto.getLocation() != null) {
            Location location = locationFinder.findById(adminPatchEventDto.getLocation().getLat(),
                    adminPatchEventDto.getLocation().getLon());
            event.setLocation(location);
        }

        eventMapper.patchUserRequest(adminPatchEventDto, event);
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
        return eventMapper.toFullDto(eventFinder.findById(userId, eventId));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = eventFinder.findById(userId, eventId);

        publishedValidation(eventId, event, updateRequest.getStateAction());
        if (updateRequest.getLocation() != null) {
            Location location = locationFinder.findById(updateRequest.getLocation().getLat(),
                    updateRequest.getLocation().getLon());
            event.setLocation(location);
        }

        eventMapper.updateUserRequest(updateRequest, event);
        log.info("Update event with eventId = {}", eventId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    private void publishedValidation(Long eventId, Event event, EventStateAction stateAction) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new AlreadyPublishedException("Event with eventId = " + eventId + "has already been published");
        }

        if (!event.getState().equals(EventState.PENDING) && stateAction.equals(EventStateAction.PUBLISH_EVENT)) {
            throw new PublicationException("The event can only be published during the pending stage");
        }

        if (stateAction != null) {
            Map<EventStateAction, EventState> state = Map.of(
                    EventStateAction.SEND_TO_REVIEW, EventState.PENDING,
                    EventStateAction.CANCEL_REVIEW, EventState.CANCELED,
                    EventStateAction.PUBLISH_EVENT, EventState.PUBLISHED,
                    EventStateAction.REJECT_EVENT, EventState.CANCELED);
            event.setState(state.get(stateAction));
        }
    }
}