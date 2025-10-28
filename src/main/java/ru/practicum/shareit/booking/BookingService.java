package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.TimeStatus;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(@NotNull BookingCreateDto createDto, @NotNull Long userId);

    BookingDto approveBooking(@NotNull Long id, @NotNull Long userId, @NotNull boolean approved);

    BookingDto getBookingDto(@NotNull Long id, @NotNull Long userId);

    List<BookingDto> getBookingByBookerIdAndStatus(@NotNull Long bookerId, @NotNull TimeStatus state);

    List<BookingDto> getBookingByOwnerId(@NotNull Long ownerId, @NotNull TimeStatus state);

}
