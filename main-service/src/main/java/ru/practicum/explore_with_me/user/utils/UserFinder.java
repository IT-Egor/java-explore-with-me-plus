package ru.practicum.explore_with_me.user.utils;

import ru.practicum.explore_with_me.user.model.User;

public interface UserFinder {
    User getUserById(Long userId);
}
