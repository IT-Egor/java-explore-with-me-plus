package ru.practicum.explore_with_me.compilations.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.compilations.dto.CreateCompilationRequest;
import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.compilations.service.CompilationService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationResponse createCompilation(@Valid @RequestBody CreateCompilationRequest createCompilationRequest) {
        log.info("Compilation POST request");
        return compilationService.create(createCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") long compilationId) {
        log.info("Compilation id={} DELETE request", compilationId);
        compilationService.deleteById(compilationId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CompilationResponse patchCompilation(@PathVariable(name = "compId") Long compilationId,
                                                @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Compilation id={} PATCH request", compilationId);
        return compilationService.update(compilationId, updateCompilationRequest);
    }
}
