package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public Item fromCreateDto(ItemCreateDto item) {
        Item fromCreateDto = new Item();
        fromCreateDto.setName(item.getName());
        fromCreateDto.setDescription(item.getDescription());
        fromCreateDto.setAvailable(item.getAvailable());
        return fromCreateDto;
    }

    public ItemDto toDto(Item item) {
        if (item == null) { //сделал проверку здесь, остальное проверяю в сервисе
            return null;
        }
        ItemDto toDto = new ItemDto();
        toDto.setId(item.getId());
        toDto.setName(item.getName());
        toDto.setDescription(item.getDescription());
        toDto.setAvailable(item.getAvailable());
        return toDto;
    }
}
