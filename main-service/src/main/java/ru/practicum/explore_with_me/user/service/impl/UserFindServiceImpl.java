package ru.practicum.explore_with_me.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.user.dao.UserRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.service.UserFindService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserFindServiceImpl implements UserFindService {
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
