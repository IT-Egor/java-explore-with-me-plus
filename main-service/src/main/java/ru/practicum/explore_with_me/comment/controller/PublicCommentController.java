package ru.practicum.explore_with_me.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.service.CommentService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentResponse> getAllCommentsByUser(@PathVariable Long eventId) {
        return commentService.getAllCommentsByEvent(eventId);
    }
}
