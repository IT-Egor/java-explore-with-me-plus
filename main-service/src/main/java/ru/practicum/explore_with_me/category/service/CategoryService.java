package ru.practicum.explore_with_me.category.service;

import ru.practicum.explore_with_me.category.dto.CategoryMergeRequest;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;

import java.util.Collection;

public interface CategoryService {
    CategoryResponse createCategory(CategoryMergeRequest categoryMergeRequest);

    void deleteCategoryById(Long categoryId);

    Collection<CategoryResponse> getCategories(int from, int size);

    CategoryResponse updateCategory(CategoryMergeRequest request, Long categoryId);

    CategoryResponse getCategoryById(Long categoryId);
}
