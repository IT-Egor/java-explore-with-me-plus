package ru.practicum.explore_with_me.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.user.dto.CreateUserRequest;
import ru.practicum.explore_with_me.user.dto.CreateUserResponse;
import ru.practicum.explore_with_me.user.model.User;

@Mapper
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User requestToUser(CreateUserRequest createUserRequest);

    CreateUserResponse userToResponse(User user);
}
