package ru.practicum.explore_with_me.event.utils.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.utils.EventFinder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventFinderImpl implements EventFinder {
    private final EventRepository eventRepository;

    @Override
    public Event findById(Long userId, Long eventId) {
        log.info("Searching event with id = {} for user with id = {}", eventId, userId);
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " and event with id = " + eventId
                        + " not found"));
    }
}