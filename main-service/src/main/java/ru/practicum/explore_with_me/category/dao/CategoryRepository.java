package ru.practicum.explore_with_me.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
