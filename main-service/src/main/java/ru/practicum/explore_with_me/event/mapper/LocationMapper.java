package ru.practicum.explore_with_me.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.event.dto.LocationDto;
import ru.practicum.explore_with_me.event.model.Location;

@Mapper
public interface LocationMapper {
    Location requestToLocation(LocationDto locationDto);

    LocationDto responseToLocationDto(Location location);
}
