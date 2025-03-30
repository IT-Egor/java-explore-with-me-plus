package ru.practicum.explore_with_me.compilations.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.compilations.dao.CompilationRepository;
import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.dto.CreateCompilationRequest;
import ru.practicum.explore_with_me.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.compilations.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilations.model.Compilation;
import ru.practicum.explore_with_me.compilations.service.CompilationService;
import ru.practicum.explore_with_me.compilations.utils.CompilationFinder;
import ru.practicum.explore_with_me.event.dao.EventRepository;

import java.util.Collection;

import static ru.practicum.explore_with_me.compilations.dao.CompilationRepository.CompilationSpecification.byPinned;


@Slf4j
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationFinder compilationFinder;
    private final EventRepository eventRepository;

    @Override
    public CompilationResponse create(CreateCompilationRequest createCompilationRequest) {
        Compilation compilation = compilationMapper.createRequestToCompilation(
                createCompilationRequest,
                eventRepository.findAllByIdIn(createCompilationRequest.getEvents()));
        CompilationResponse response = compilationMapper.compilationToResponse(compilationRepository.save(compilation));
        log.info("Compilation with id={} was created", response.getId());
        return response;
    }

    @Override
    public CompilationResponse getCompilationById(Long compId) {
        Compilation compilation = compilationFinder.findById(compId);
        log.info("Compilation with id={} was found", compilation.getId());
        return compilationMapper.compilationToResponse(compilation);
    }

    @Override
    public Collection<CompilationResponse> getCompilations(Boolean pinned, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Compilation> specification = Specification.where(byPinned(pinned));
        Page<Compilation> page = compilationRepository.findAll(specification, pageable);

        log.info("Get compilations with {from, size, pinned}={},{},{}", from, size, pinned);

        return page.getContent().stream().map(compilationMapper::compilationToResponse).toList();
    }

    @Override
    public void deleteById(Long compilationId) {
        compilationFinder.findById(compilationId);
        compilationRepository.deleteById(compilationId);
        log.info("Compilation with id={} was deleted", compilationId);
    }

    @Override
    public CompilationResponse update(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationFinder.findById(compilationId);
        compilationMapper.compilationUpdateRequest(
                updateCompilationRequest,
                compilation,
        eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
        CompilationResponse response = compilationMapper.compilationToResponse(
                compilationRepository.save(compilation));
        log.info("Compilation with id={} was updated", response.getId());
        return response;
    }
}
