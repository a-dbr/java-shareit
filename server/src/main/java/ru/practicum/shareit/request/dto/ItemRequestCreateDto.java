package ru.practicum.shareit.request.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
public class ItemRequestCreateDto {
    @NotBlank(message = "Описание запроса не должно быть пустым")
    private String description;
}

