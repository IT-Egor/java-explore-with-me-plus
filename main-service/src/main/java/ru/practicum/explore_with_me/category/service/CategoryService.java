package ru.practicum.explore_with_me.category.service;

import ru.practicum.explore_with_me.category.dto.CategoryCreateRequest;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;

import java.util.Collection;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest);

    void deleteCategoryById(Long categoryId);

    Collection<CategoryResponse> getCategories(int from, int size);

    CategoryResponse getCategoryById(Long categoryId);
}
