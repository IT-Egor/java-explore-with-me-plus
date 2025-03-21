package ru.practicum.explore_with_me.category.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.category.dto.CategoryCreateRequest;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;
import ru.practicum.explore_with_me.category.service.CategoryService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        log.info("Create category request");
        return categoryService.createCategory(request);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "catId") Long categoryId) {
        log.info("Delete category request");
        categoryService.deleteCategoryById(categoryId);
    }


}
