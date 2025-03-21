package ru.practicum.explore_with_me.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;
import ru.practicum.explore_with_me.category.service.CategoryService;

import java.util.Collection;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryResponse> getCategories(@PositiveOrZero(message = "from must be greater or equal to zero")
                                                      @RequestParam(defaultValue = "0") int from,

                                                      @Positive(message = "size must be greater than zero")
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Get categories request");
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getCategoryById(@PathVariable(name = "catId") Long categoryId) {
        log.info("Get category with id={} request", categoryId);
        return categoryService.getCategoryById(categoryId);
    }
}
