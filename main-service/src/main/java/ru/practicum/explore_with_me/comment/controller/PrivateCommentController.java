package ru.practicum.explore_with_me.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.CreateCommentRequest;
import ru.practicum.explore_with_me.comment.service.CommentService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@Valid @RequestBody CreateCommentRequest request, @PathVariable Long userId) {
        return commentService.createComment(request, userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.deleteCommentByIdAndAuthor(commentId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentResponse> getAllCommentsByUser(@PathVariable Long userId) {
        return commentService.getAllCommentsByUser(userId);
    }
}
