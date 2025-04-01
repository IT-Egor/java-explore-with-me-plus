package ru.practicum.explore_with_me.comment.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.comment.dao.CommentRepository;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.CreateCommentRequest;
import ru.practicum.explore_with_me.comment.mapper.CommentMapper;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.service.CommentService;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.model.User;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentResponse createComment(CreateCommentRequest createCommentRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d not found", userId)));
        Event event = eventRepository.findById(createCommentRequest.getEventId()).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%d not found", createCommentRequest.getEventId())));

        Comment comment = commentMapper.requestToComment(createCommentRequest, event, user);
        CommentResponse response = commentMapper.commentToResponse(commentRepository.save(comment));
        log.info("Comment id={} was created by user id={}", response.getId(), response.getAuthor().getId());
        return response;
    }
}
