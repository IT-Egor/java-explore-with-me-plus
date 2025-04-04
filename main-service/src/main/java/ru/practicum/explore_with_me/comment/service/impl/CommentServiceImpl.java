package ru.practicum.explore_with_me.comment.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.comment.dao.CommentRepository;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.CreateCommentRequest;
import ru.practicum.explore_with_me.comment.mapper.CommentMapper;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.service.CommentService;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.error.model.PublicationException;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.model.User;

import java.util.Collection;

@Slf4j
@Service
@Transactional
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

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new PublicationException("Event must be published");
        }

        Comment comment = commentMapper.requestToComment(createCommentRequest, event, user);
        CommentResponse response = commentMapper.commentToResponse(commentRepository.save(comment));
        log.info("Comment id={} was created by user id={}", response.getId(), response.getAuthor().getId());
        return response;
    }

    @Override
    public void deleteCommentByIdAndAuthor(Long commentId, Long userId) {
        if (commentRepository.deleteCommentByIdAndAuthor_Id(commentId, userId) != 0) {
            log.info("Comment with id={} was deleted by user id={}", commentId, userId);
        } else {
            throw new NotFoundException(String.format("Comment with id=%d by author id=%d was not found", commentId, userId));
        }
    }

    @Override
    public void deleteCommentById(Long commentId) {
        if (commentRepository.deleteCommentById(commentId) != 0) {
            log.info("Comment with id={} was deleted", commentId);
        } else {
            throw new NotFoundException(String.format("Comment with id=%d was not found", commentId));
        }
    }

    @Override
    public Collection<CommentResponse> getAllCommentsByUser(Long userId, Integer from, Integer size) {
        return commentRepository.findAllByAuthor_IdOrderByPublishedOnDesc(userId, createPageable(from, size))
                .stream()
                .map(commentMapper::commentToResponse)
                .toList();
    }

    @Override
    public Collection<CommentResponse> getAllCommentsByEvent(Long eventId, Integer from, Integer size) {
        return commentRepository.findAllByEvent_IdOrderByPublishedOnDesc(eventId, createPageable(from, size))
                .stream()
                .map(commentMapper::commentToResponse)
                .toList();
    }

    private Pageable createPageable(Integer from, Integer size) {
        int pageNumber = from / size;
        return  PageRequest.of(pageNumber, size);
    }
}
