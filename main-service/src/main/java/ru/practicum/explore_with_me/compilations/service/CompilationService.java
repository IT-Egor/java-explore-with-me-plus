package ru.practicum.explore_with_me.compilations.service;

import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.dto.CreateCompilationRequest;
import ru.practicum.explore_with_me.compilations.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    CompilationResponse create(CreateCompilationRequest createCompilationRequest);

    void deleteById(Long compilationId);

    CompilationResponse getCompilationById(Long compId);

    Collection<CompilationResponse> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationResponse update(Long compilationId, UpdateCompilationRequest updateCompilationRequest);
}
