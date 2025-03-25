package ru.practicum.explore_with_me.event.utils;

import ru.practicum.explore_with_me.event.model.Event;

import java.util.Set;

public interface EventFinder {
    Event findByIdAndInitiatorId(Long userId, Long eventId);

    Event findById(Long eventId);

    Set<Event> findAllByIdIn(Set<Long> ids);
}