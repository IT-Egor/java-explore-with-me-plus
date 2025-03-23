package ru.practicum.explore_with_me.category.utils.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.category.dao.CategoryRepository;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.utils.CategoryFinder;
import ru.practicum.explore_with_me.error.model.NotFoundException;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class CategoryFinderImpl implements CategoryFinder {
    private final CategoryRepository repository;

    @Override
    public Category findById(Long categoryId) {
        Optional<Category> categoryOpt = repository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            log.info("Category with id={} found", categoryId);
            return categoryOpt.get();
        } else {
            log.warn("Category with id={} not found", categoryId);
            throw new NotFoundException(String.format("Category with id=%d not found", categoryId));
        }
    }
}
