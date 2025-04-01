package ru.practicum.explore_with_me.comment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.comment.model.Comment;

import java.util.Collection;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long deleteCommentByIdAndAuthor_Id(Long id, Long authorId);

    long deleteCommentById(Long commentId);

    Collection<Comment> findAllByAuthor_IdOrderByPublishedOnDesc(Long userId);

    Collection<Comment> findAllByEvent_IdOrderByPublishedOnDesc(Long eventId);
}
