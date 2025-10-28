package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

// Класс создал для обхода цикла зависимостей бинов
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingQueryServiceImpl implements BookingQueryService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto getLastBookingByItemId(Long itemId) {
        Booking booking = bookingRepository.findTopByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getNextBookingByItemId(Long itemId) {
        Booking booking = bookingRepository.findTopByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
        return bookingMapper.toDto(booking);
    }

    @Override
    public boolean isBooked(Long itemId, Long userId) {
        return bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId,
                userId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );
    }
}
