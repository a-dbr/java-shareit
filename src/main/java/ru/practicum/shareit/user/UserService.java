package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto user);

    UserDto updateUser(Long userId, UserUpdateDto user);

    UserDto getUserDto(Long userId);

    User getUser(Long userId);

    boolean isUserExist(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);
}