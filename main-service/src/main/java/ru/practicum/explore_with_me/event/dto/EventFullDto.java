package ru.practicum.explore_with_me.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;
import ru.practicum.explore_with_me.event.model.EventState;
import ru.practicum.explore_with_me.event.model.Location;
import ru.practicum.explore_with_me.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryResponse category;
    Long confirmedRequests;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit = 0;
    LocalDateTime publishedOn;
    Boolean requestModeration = true;
    EventState state;
    String title;
    Long views;
}