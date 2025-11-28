package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.Instant;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Instant created;
    private List<ItemShortDto> items;
}
