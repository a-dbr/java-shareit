package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public Item fromCreateDto(ItemCreateDto item) {
        Item fromCreateDto = new Item();
        fromCreateDto.setName(item.getName());
        fromCreateDto.setDescription(item.getDescription());
        fromCreateDto.setAvailable(item.getAvailable());
        fromCreateDto.setOwnerId(item.getOwnerId());
        return fromCreateDto;
    }

    public Item fromUpdateDto(ItemUpdateDto item) {
        Item fromUpdateDto = new Item();
        fromUpdateDto.setName(item.getName());
        fromUpdateDto.setDescription(item.getDescription());
        fromUpdateDto.setAvailable(item.getAvailable());
        return fromUpdateDto;
    }

    public ItemDto toDto(Item item) {
        ItemDto toDto = new ItemDto();
        toDto.setId(item.getId());
        toDto.setName(item.getName());
        toDto.setDescription(item.getDescription());
        toDto.setAvailable(item.getAvailable());
        return toDto;
    }
}
