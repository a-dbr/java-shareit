package ru.practicum.shareit.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException() {
        super("Вещь недоступна для бронирования");
    }
}
