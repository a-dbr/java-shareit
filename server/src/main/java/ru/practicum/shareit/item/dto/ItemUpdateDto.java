package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemUpdateDto {
    @Nullable
    @Size(max = 100)
    private String name;

    @Nullable
    @Size(max = 200)
    private String description;

    @Nullable
    private Boolean available;
}
