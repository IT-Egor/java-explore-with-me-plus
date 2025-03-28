package ru.practicum.explore_with_me.compilations.utils;

import ru.practicum.explore_with_me.compilations.model.Compilation;

public interface CompilationFinder {
    Compilation findById(Long compilationId);
}
