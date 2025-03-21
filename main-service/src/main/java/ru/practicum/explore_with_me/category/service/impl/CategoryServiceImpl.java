package ru.practicum.explore_with_me.category.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.category.dao.CategoryRepository;
import ru.practicum.explore_with_me.category.dto.CategoryCreateRequest;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.service.CategoryService;
import ru.practicum.explore_with_me.category.utils.CategoryFinder;
import ru.practicum.explore_with_me.error.model.AlreadyExistsException;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryFinder categoryFinder;

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

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        log.info("Get category with id={}", categoryId);
        return categoryMapper.categoryToResponse(categoryFinder.findById(categoryId));
    }

    @Override
    public Collection<CategoryResponse> getCategories(int from, int size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Category> page= categoryRepository.findAll(pageable);

        log.info("Get users with {from, size} = ({}, {})", from, size);
        return page.getContent().stream().map(categoryMapper::categoryToResponse).toList();
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        categoryFinder.findById(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("Category with id={} was deleted", categoryId);
    }
}
