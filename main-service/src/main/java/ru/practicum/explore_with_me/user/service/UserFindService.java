package ru.practicum.explore_with_me.user.service;

import ru.practicum.explore_with_me.user.model.User;

public interface UserFindService {
    User getUserById(Long userId);
}
