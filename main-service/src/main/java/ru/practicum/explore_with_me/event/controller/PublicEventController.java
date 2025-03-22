package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.event.dto.EventFullDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class PublicEventController {
    public Collection<EventFullDto> getAllEvents(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<String> states,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0",required = false) Long from,
            @RequestParam(defaultValue = "10",required = false) Long size
            ){
        return null;
    }
}
