package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT DISTINCT r FROM ItemRequest r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.requester.id = :requesterId " +
            "ORDER BY r.created DESC")
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDescWithItems(@Param("requesterId") Long requesterId);

    @Query("SELECT DISTINCT r FROM ItemRequest r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.requester.id <> :requesterId " +
            "ORDER BY r.created DESC")
    List<ItemRequest> findAllByRequesterIdNotWithItems(@Param("requesterId") Long requesterId, Pageable pageable);

    @Query("SELECT r FROM ItemRequest r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.id = :id")
    Optional<ItemRequest> findByIdWithItems(@Param("id") Long id);
}
