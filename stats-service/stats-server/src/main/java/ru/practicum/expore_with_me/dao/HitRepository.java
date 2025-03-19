package ru.practicum.expore_with_me.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.expore_with_me.model.Hit;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long>, QuerydslPredicateExecutor<Hit> {
}
