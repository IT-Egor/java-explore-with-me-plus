package ru.practicum.explore_with_me.comment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.comment.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
