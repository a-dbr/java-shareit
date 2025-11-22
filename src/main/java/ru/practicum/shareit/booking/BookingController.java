package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.TimeStatus;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                    @Valid @RequestBody BookingCreateDto booking) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBookingDto(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL")
                                                  TimeStatus state) {
        return bookingService.getBookingByBookerIdAndStatus(userId, state);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> getBookingsByItemOwnerId(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL")
                                                     TimeStatus state) {
        return bookingService.getBookingByOwnerId(userId, state);
    }

}