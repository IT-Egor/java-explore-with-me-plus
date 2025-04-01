package ru.practicum.explore_with_me.comment.service;

import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.CreateCommentRequest;

public interface CommentService {
    CommentResponse createComment(CreateCommentRequest createCommentRequest, Long userId);

    void deleteComment(Long commentId, Long userId);
}
