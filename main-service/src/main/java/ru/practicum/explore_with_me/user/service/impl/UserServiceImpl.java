package ru.practicum.explore_with_me.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.error.model.EmailAlreadyExistsException;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.dto.CreateUserRequest;
import ru.practicum.explore_with_me.user.dto.CreateUserResponse;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.service.UserFindService;
import ru.practicum.explore_with_me.user.service.UserService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserFindService userFindService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        try {
            User user = userMapper.requestToUser(createUserRequest);
            return userMapper.userToResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("users_email_key")) {
                throw new EmailAlreadyExistsException(String.format("User with email %s already exists", createUserRequest.getEmail()));
            } else {
                throw e;
            }
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        userFindService.getUserById(userId);
        userRepository.deleteById(userId);
    }
}
