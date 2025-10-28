package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface ItemMapper {

    Item fromCreateDto(ItemCreateDto dto);

    @Mapping(source = "owner", target = "ownerId")
    ItemDto toDto(Item item);

    default Long map(User owner) {
        return owner == null ? null : owner.getId();
    }

    ItemWithBookingsDto toDtoWithBookings(Item item);

    void updateItemFromDto(ItemUpdateDto dto, @MappingTarget Item entity);
}