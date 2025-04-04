package ru.practicum.explore_with_me.comment.service;

import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.MergeCommentRequest;

import java.util.Collection;

public interface CommentService {
    CommentResponse createComment(MergeCommentRequest mergeCommentRequest, Long userId);

    void deleteCommentByIdAndAuthor(Long commentId, Long userId);

    Collection<CommentResponse> getAllCommentsByUser(Long userId, Integer from, Integer size);

    void deleteCommentById(Long commentId);

    Collection<CommentResponse> getAllCommentsByEvent(Long eventId, Integer from, Integer size);

    CommentResponse updateCommentByIdAndAuthorId(Long commentId, Long userId, MergeCommentRequest request);
}
