package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemWithBookingsDto getItemDto(@NotNull Long itemId);

    Item getItem(@NotNull Long itemId);

    List<ItemWithBookingsDto> getItemsByOwnerId(@NotNull Long ownerId);

    ItemDto createItem(@NotNull ItemCreateDto itemDto, @NotNull Long ownerId);

    ItemDto updateItem(@NotNull Long itemId, @NotNull ItemUpdateDto itemUpdateDto, @NotNull Long ownerId);

    void deleteItem(@NotNull Long itemId, @NotNull Long userId);

    List<ItemDto> getItemsByText(String text);

    CommentDto postComment(@NotNull CommentCreateDto commentCreateDto,
                           @NotNull Long itemId,
                           @NotNull Long userId);

    boolean checkItemOwner(@NotNull Long itemId, @NotNull Long userId);

    boolean existById(@NotNull Long itemId);
}
