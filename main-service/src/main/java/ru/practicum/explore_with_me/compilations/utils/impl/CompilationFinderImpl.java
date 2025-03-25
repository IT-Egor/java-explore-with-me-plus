package ru.practicum.explore_with_me.compilations.utils.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.compilations.dao.CompilationRepository;
import ru.practicum.explore_with_me.compilations.model.Compilation;
import ru.practicum.explore_with_me.compilations.utils.CompilationFinder;
import ru.practicum.explore_with_me.error.model.NotFoundException;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class CompilationFinderImpl implements CompilationFinder {
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation findById(Long compilationId) {
        Optional<Compilation> compilationOpt = compilationRepository.findById(compilationId);
        if (compilationOpt.isPresent()) {
            log.info("Compilation with id={} was found", compilationId);
            return compilationOpt.get();
        } else {
            log.warn("Compilation with id={} not found", compilationId);
            throw new NotFoundException(String.format("Compilation with id=%s, not found", compilationId));
        }
    }
}
