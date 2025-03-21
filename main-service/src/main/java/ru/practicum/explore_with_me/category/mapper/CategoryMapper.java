package ru.practicum.explore_with_me.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.category.dto.CategoryMergeRequest;
import ru.practicum.explore_with_me.category.dto.CategoryResponse;
import ru.practicum.explore_with_me.category.model.Category;

@Mapper
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category requestToCategory(CategoryMergeRequest categoryMergeRequest);

    CategoryResponse categoryToResponse(Category category);
}
