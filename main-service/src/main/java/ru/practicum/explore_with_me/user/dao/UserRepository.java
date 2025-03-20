package ru.practicum.explore_with_me.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
