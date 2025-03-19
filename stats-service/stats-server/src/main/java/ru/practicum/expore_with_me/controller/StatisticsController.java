package ru.practicum.expore_with_me.controller;

import dto.GetResponse;
import dto.HitRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.expore_with_me.service.StatisticsService;

import java.util.Collection;
import java.util.Map;

@RestController
@AllArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void addHit(@Valid @RequestBody HitRequest hitRequest) {
        statisticsService.createHit(hitRequest);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    Collection<GetResponse> getStatistics(@RequestParam Map<String, String> params) {
        throw new RuntimeException("Not implemented");
    }
}
