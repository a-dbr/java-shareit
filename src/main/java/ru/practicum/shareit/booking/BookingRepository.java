package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndEndBeforeOrderByEndDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = :bookerId
          AND :currentTime BETWEEN b.start AND b.end
        ORDER BY b.start DESC
    """)
    List<Booking> findByBookerIdAndCurrentTime(@Param("bookerId") Long bookerId,
                                               @Param("currentTime") LocalDateTime currentTime);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByEndDesc(Long ownerId, LocalDateTime endTime);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND :currentTime BETWEEN b.start AND b.end
        ORDER BY b.start DESC
    """)
    List<Booking> findByItemOwnerIdAndCurrent(@Param("ownerId") Long ownerId,
                                              @Param("currentTime") LocalDateTime current);

    Booking findTopByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

    Booking findTopByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId,
                                                           Long bookerId,
                                                           BookingStatus status,
                                                           LocalDateTime end);
}
