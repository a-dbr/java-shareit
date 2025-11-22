package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(@NotNull UserCreateDto user);

    UserDto updateUser(@NotNull Long userId, @NotNull UserUpdateDto user);

    UserDto getUserDto(@NotNull Long userId);

    User getUser(@NotNull Long userId);

    boolean isUserExist(@NotNull Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(@NotNull Long userId);
}