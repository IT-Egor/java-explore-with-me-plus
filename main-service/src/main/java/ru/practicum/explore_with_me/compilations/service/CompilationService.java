package ru.practicum.explore_with_me.compilations.service;

import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.dto.CompilationMergeRequest;

public interface CompilationService {
    CompilationResponse create(CompilationMergeRequest compilationMergeRequest);
}
