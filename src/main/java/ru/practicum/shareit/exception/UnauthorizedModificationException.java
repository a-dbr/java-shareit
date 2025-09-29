package ru.practicum.shareit.exception;

public class UnauthorizedModificationException extends RuntimeException {
    public UnauthorizedModificationException(String message) {
        super(message);
    }
}