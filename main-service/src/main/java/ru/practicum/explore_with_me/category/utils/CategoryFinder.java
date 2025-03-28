package ru.practicum.explore_with_me.category.utils;

import ru.practicum.explore_with_me.category.model.Category;

public interface CategoryFinder {
    Category findById(Long categoryId);
}
