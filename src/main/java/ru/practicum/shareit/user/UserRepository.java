package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User findById(Long id);

    List<User> findAll();

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    void deleteById(Long id);
}
