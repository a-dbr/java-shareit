package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreateDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyUsedException(user.getEmail());
        }
        User newUser = UserMapper.fromDto(user);
        newUser = userRepository.save(newUser);
        return UserMapper.toDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto user) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        User existingUser = userRepository.findById(userId);

        String newEmail = user.getEmail();
        if (newEmail != null &&
                !newEmail.equalsIgnoreCase(existingUser.getEmail()) &&
                userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyUsedException(newEmail);
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (newEmail != null) {
            existingUser.setEmail(newEmail);
        }

        return UserMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    public UserDto getUserDto(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return UserMapper.toDto(userRepository.findById(userId));
    }

    @Override
    public User getUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return userRepository.findById(userId);
    }

    @Override
    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
