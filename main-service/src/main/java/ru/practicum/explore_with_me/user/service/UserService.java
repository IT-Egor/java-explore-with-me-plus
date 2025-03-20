package ru.practicum.explore_with_me.user.service;

import ru.practicum.explore_with_me.user.dto.CreateUserRequest;
import ru.practicum.explore_with_me.user.dto.CreateUserResponse;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest createUserRequest);

    void deleteUserById(Long userId);
}
