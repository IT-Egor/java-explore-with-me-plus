package ru.practicum.explore_with_me.compilations.service;

import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.dto.CompilationMergeRequest;

import java.util.Collection;

public interface CompilationService {
    CompilationResponse create(CompilationMergeRequest compilationMergeRequest);

    void deleteById(Long compilationId);

    CompilationResponse getCompilationById(Long compId);

    Collection<CompilationResponse> getCompilations(Boolean pinned, Integer from, Integer size);
}
