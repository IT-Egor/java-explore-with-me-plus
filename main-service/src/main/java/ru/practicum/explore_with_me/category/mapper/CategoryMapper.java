package ru.practicum.explore_with_me.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.category.dto.CategoryCreateRequest;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;
import ru.practicum.explore_with_me.category.model.Category;

@Mapper
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category requestToCategory(CategoryCreateRequest categoryCreateRequest);

    CategoryResponse categoryToResponse(Category category);
}
