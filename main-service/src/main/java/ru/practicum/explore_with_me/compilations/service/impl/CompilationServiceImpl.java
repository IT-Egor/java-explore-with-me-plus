package ru.practicum.explore_with_me.compilations.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.compilations.dao.CompilationRepository;
import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.dto.CompilationMergeRequest;
import ru.practicum.explore_with_me.compilations.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilations.model.Compilation;
import ru.practicum.explore_with_me.compilations.service.CompilationService;
import ru.practicum.explore_with_me.event.utils.EventFinder;

@Slf4j
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventFinder eventFinder;

    @Override
    public CompilationResponse create(CompilationMergeRequest compilationMergeRequest) {
        Compilation compilation = compilationMapper.createRequestToCompilation(
                compilationMergeRequest,
                eventFinder.findAllByIdIn(compilationMergeRequest.getEvents()));
        CompilationResponse response = compilationMapper.compilationToResponse(compilationRepository.save(compilation));
        log.info("Compilation with id={} was created", response.getId());
        return compilationMapper.compilationToResponse(compilation);
    }
}
