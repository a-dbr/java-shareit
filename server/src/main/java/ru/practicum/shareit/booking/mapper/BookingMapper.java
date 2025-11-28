package ru.practicum.shareit.booking.mapper;

import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto toDto(@NotNull Booking booking);
}