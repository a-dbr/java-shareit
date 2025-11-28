package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", uses = { CommentMapper.class })
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item fromCreateDto(ItemCreateDto dto);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto toDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item updateItem(ItemUpdateDto dto, @MappingTarget Item item);

    ItemWithBookingsDto toDtoWithBookings(Item item);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemShortDto toShortDto(Item item);
}