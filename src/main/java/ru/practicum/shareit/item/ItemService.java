package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;


import java.util.List;

public interface ItemService {
    ItemDto getItemDto(Long itemId);

    Item getItem(Long itemId);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

    ItemDto createItem(ItemCreateDto itemDto, Long ownerId);

    ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId);

    void deleteItem(Long itemId, Long userId);

    List<ItemDto> getItemsByText(String text);
}
