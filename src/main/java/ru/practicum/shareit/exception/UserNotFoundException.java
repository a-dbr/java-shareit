package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long id) {
    super("Пользователь с ID " + id + " не найден");
  }
}

