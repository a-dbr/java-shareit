package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    @Future
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    @Positive(message = "Id не может быть отрицательным числом")
    private Long itemId;
}
