package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
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

    Item fromCreateDto(@NotNull ItemCreateDto dto);

    @Mapping(source = "owner", target = "ownerId")
    ItemDto toDto(@NotNull Item item);

    default Long map(@NotNull User owner) {
        return owner.getId();
    }

    ItemWithBookingsDto toDtoWithBookings(Item item);

    void updateItemFromDto(ItemUpdateDto dto, @MappingTarget Item entity);
}