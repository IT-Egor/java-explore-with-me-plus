package ru.practicum.explore_with_me.event.service;

import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EventService {
    Collection<EventShortDto> getAllEvents(Long userId, Integer from, Integer size);

    Collection<EventFullDto> getAllEvents(Set<Long> users, Set<String> states, Set<Long> categories,
                                          LocalDateTime rangeStart,LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);
}