package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring", uses = { ItemMapper.class })
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "items", ignore = true)
    ItemRequest fromCreateDto(ItemRequestCreateDto dto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "items", source = "items")
    ItemRequestDto toItemRequestDto(ItemRequest request);
}
