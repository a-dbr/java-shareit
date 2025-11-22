package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User id must be positive") Long userId,
            @RequestBody @Valid ItemRequestCreateDto createDto) {

        return itemRequestService.create(userId, createDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User id must be positive") Long userId) {

        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOtherRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User id must be positive") Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero(message = "from must be >= 0") int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive(message = "size must be > 0") int size) {

        return itemRequestService.getAllOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "User id must be positive") Long userId,
            @PathVariable("requestId") Long requestId) {

        return itemRequestService.getById(userId, requestId);
    }
}
