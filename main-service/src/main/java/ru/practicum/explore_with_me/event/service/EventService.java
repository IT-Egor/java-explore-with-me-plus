package ru.practicum.explore_with_me.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.model.enums.SortType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EventService {
    Collection<EventShortDto> getAllEvents(Long userId, Integer from, Integer size);

    Collection<EventFullDto> getAllEventsAdmin(Set<Long> users, Set<String> states, Set<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                               Integer size);

    Collection<EventShortDto> getAllEventsPublic(String text,
                                                 Set<Long> categories,
                                                 Boolean paid,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable,
                                                 SortType sort,
                                                 Integer from,
                                                 Integer size,
                                                 HttpServletRequest httpServletRequest);

    EventFullDto patchEventById(Long eventId, AdminPatchEventDto adminPatchEventDto);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);
}