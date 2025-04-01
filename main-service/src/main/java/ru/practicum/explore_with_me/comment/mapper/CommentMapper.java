package ru.practicum.explore_with_me.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.CreateCommentRequest;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.model.User;

@Mapper
public interface CommentMapper {
    @Mapping(target = "author", source = "user")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", source = "commentRequest.publishedOn")
    Comment requestToComment(CreateCommentRequest commentRequest, Event event, User user);

    CommentResponse commentToResponse(Comment comment);
}
