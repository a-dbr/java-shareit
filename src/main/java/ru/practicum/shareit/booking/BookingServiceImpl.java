package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.TimeStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreateDto createDto, Long userId) {
        User user = userService.getUser(userId);
        Item item = itemService.getItem(createDto.getItemId());

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException();
        }

        if (createDto.getStart().isAfter(createDto.getEnd())) {
            throw new IllegalArgumentException("Дата начала бронирования не может быть позже даты окончания.");
        }

        Booking booking = new Booking();
        booking.setItem(itemService.getItem(createDto.getItemId()));
        booking.setBooker(user);
        booking.setStart(createDto.getStart());
        booking.setEnd(createDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long id, Long userId, boolean approved) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        checkBooking(booking, userId);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingDto(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        boolean isBooker = booking.getBooker() != null && Objects.equals(booking.getBooker().getId(), userId);
        boolean isItemOwner = booking.getItem() != null
                && itemService.checkItemOwner(booking.getItem().getId(), userId);

        if (!isBooker && !isItemOwner) {
            throw new AccessDeniedException("Доступ разрешён автору бронирования либо владельцу вещи");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingByBookerIdAndStatus(Long bookerId, TimeStatus status) {
        if (!userService.isUserExist(bookerId)) {
            throw new NotFoundException("Пользователь с ID = %d не найден".formatted(bookerId));
        }
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        bookings = switch (status) {
            case WAITING ->
                 bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED ->
                bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            case ALL ->
                bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
            case PAST ->
                bookingRepository.findByBookerIdAndEndBeforeOrderByEndDesc(bookerId, now);
            case FUTURE ->
                bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case CURRENT ->
                bookingRepository.findByBookerIdAndCurrentTime(bookerId, now);
        };
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingByOwnerId(Long ownerId, TimeStatus status) {
        if (!userService.isUserExist(ownerId)) {
            throw new NotFoundException("Пользователь с ID = %d не найден".formatted(ownerId));
        }
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        bookings = switch (status) {
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            case ALL ->
                    bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case PAST ->
                    bookingRepository.findByItemOwnerIdAndEndBeforeOrderByEndDesc(ownerId, now);
            case FUTURE ->
                    bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndCurrent(ownerId, now);
        };
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    private void checkBooking(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Подтвердить бронирование может только владелец вещи");
        }
    }
}
