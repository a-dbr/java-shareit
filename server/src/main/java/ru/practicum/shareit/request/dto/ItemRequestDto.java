package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.Instant;
import java.util.Set;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private Instant created;
    private Set<ItemShortDto> items;
}
