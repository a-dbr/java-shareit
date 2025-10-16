package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long userIdCounter = 1L;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(userIdCounter);
            userIdCounter++;
        }
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
}
