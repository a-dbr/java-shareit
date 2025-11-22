package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemCreateDto {
    @NotNull
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private Boolean available;
}
