package ru.practicum.explore_with_me.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class RequestDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;

    Long event;
    Long id;
    Long requester;
    String status;
}
