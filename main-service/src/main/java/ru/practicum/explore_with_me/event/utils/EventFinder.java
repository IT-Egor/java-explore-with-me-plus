package ru.practicum.explore_with_me.event.utils;

import ru.practicum.explore_with_me.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EventFinder {
    Event findById(Long userId, Long eventId);
}