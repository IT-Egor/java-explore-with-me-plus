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
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponse createCompilation(@Valid @RequestBody CreateCompilationRequest createCompilationRequest) {
        return compilationService.create(createCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponse patchCompilation(@PathVariable Long compId,
                                                @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.update(compId, updateCompilationRequest);
    }
}
