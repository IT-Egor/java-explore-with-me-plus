package ru.practicum.explore_with_me.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.model.Request;

@Mapper
public interface RequestMapper {
    RequestDto toRequestDto(Request request);
}
