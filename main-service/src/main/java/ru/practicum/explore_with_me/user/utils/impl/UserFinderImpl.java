package ru.practicum.explore_with_me.user.utils.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.utils.UserFinder;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class UserFinderImpl implements UserFinder {
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            log.info("User with id={} was found", userId);
            return userOpt.get();
        } else {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException(String.format("User with id=%d + not found", userId));
        }
    }
}
