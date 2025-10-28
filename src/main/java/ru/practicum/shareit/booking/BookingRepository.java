package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatusOrderByStartAsc(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartAsc(Long bookerId);

    List<Booking> findByBookerIdAndEndBeforeOrderByEndAsc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartAsc(Long bookerId, LocalDateTime start);

    @Query("""
        SELECT b FROM bookings b
        WHERE b.booker.id = :bookerId
          AND :currentTime BETWEEN b.start AND b.end
        ORDER BY b.start ASC
    """)
    List<Booking> findByBookerIdAndCurrentTime(@Param("bookerId") Long bookerId,
                                               @Param("currentTime") LocalDateTime currentTime);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartAsc(Long ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartAsc(Long ownerId);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByEndAsc(Long ownerId, LocalDateTime endTime);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartAsc(Long ownerId, LocalDateTime start);

    @Query("""
        SELECT b FROM bookings b
        WHERE b.item.owner.id = :ownerId
          AND :currentTime BETWEEN b.start AND b.end
        ORDER BY b.start ASC
    """)
    List<Booking> findByItemOwnerIdAndCurrent(@Param("ownerId") Long ownerId,
                                              @Param("currentTime") LocalDateTime current);

    Booking findTopByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

    Booking findTopByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    boolean existsByItemIdAndBookerIdAndStatusAndStartBefore(Long itemId,
                                                             Long bookerId,
                                                             BookingStatus status,
                                                             LocalDateTime start);
}
