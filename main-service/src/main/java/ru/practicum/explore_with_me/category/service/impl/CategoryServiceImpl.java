package ru.practicum.explore_with_me.category.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.category.dao.CategoryRepository;
import ru.practicum.explore_with_me.category.dto.CategoryCreateRequest;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.service.CategoryService;
import ru.practicum.explore_with_me.error.model.AlreadyExistsException;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        try {
            Category category = categoryMapper.requestToCategory(categoryCreateRequest);
            CategoryResponse categoryResponse = categoryMapper.categoryToResponse(categoryRepository.save(category));
            log.info("Category with id={} was created", categoryResponse.getId());
            return categoryResponse;
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("categories_name_key")) {
                log.warn("Category with name '{}' already exists", categoryCreateRequest.getName());
                throw new AlreadyExistsException(String.format("Category with name '%s' already exists", categoryCreateRequest.getName()));
            } else {
                throw e;
            }
        }
    }
}
