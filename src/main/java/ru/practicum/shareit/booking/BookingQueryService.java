package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingQueryService {
    BookingDto getLastBookingByItemId(@NotNull Long itemId);

    BookingDto getNextBookingByItemId(@NotNull Long itemId);

    boolean isBooked(@NotNull Long itemId, @NotNull Long bookerId);
}