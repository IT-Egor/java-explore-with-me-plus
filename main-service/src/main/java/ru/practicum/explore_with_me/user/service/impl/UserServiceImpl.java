package ru.practicum.explore_with_me.user.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.error.model.EmailAlreadyExistsException;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.dto.CreateUserRequest;
import ru.practicum.explore_with_me.user.dto.UserResponse;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.service.UserService;
import ru.practicum.explore_with_me.user.utils.UserFinder;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserFinder userFinder;

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        try {
            User user = userMapper.requestToUser(createUserRequest);
            UserResponse userResponse = userMapper.userToResponse(userRepository.save(user));
            log.info("User with id={} was created", userResponse.getId());
            return userResponse;
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("users_email_key")) {
                log.warn("User with email '{}' already exists", createUserRequest.getEmail());
                throw new EmailAlreadyExistsException(String.format("User with email '%s' already exists", createUserRequest.getEmail()));
            } else {
                throw e;
            }
        }
    }

    @Override
    public Collection<UserResponse> getUsers(List<Long> userIds, int from, int size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<User> page;
        if (userIds != null && !userIds.isEmpty()) {
            page = userRepository.findUsersByIdIn(userIds, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        log.info("Get users with {ids, from, size} = ({}, {}, {})", userIds, from, size);
        return page.getContent().stream().map(userMapper::userToResponse).toList();
    }

    @Override
    public void deleteUserById(Long userId) {
        userFinder.getUserById(userId);
        userRepository.deleteById(userId);
        log.info("User with id={} was deleted", userId);
    }
}
