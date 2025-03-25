package ru.practicum.explore_with_me.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.compilations.dto.CompilationMergeRequest;
import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.model.Compilation;
import ru.practicum.explore_with_me.event.model.Event;

import java.util.Set;

@Mapper
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", defaultValue = "false")
    Compilation createRequestToCompilation(CompilationMergeRequest compilationMergeRequest, Set<Event> events);

    CompilationResponse compilationToResponse(Compilation compilation);
}
