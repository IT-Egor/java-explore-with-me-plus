package ru.practicum.explore_with_me.user.utils.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.utils.UserFinder;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserFinderImpl implements UserFinder {
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            throw new NotFoundException(String.format("User with id %s + not found", userId));
        }
    }
}
