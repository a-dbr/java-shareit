package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedModificationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto getItemDto(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemCreateDto itemDto, Long ownerId) {
        if (itemDto == null) {
            throw new IllegalArgumentException("ItemCreateDto не должен быть null");
        }
        if (!userService.isUserExist(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }
        Item item = ItemMapper.fromCreateDto(itemDto);
        item.setOwnerId(ownerId);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        if (itemUpdateDto == null) {
            throw new IllegalArgumentException("ItemUpdateDto не должен быть null");
        }
        Item item = getItem(itemId);
        checkItemOwner(item, ownerId);
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public void deleteItem(Long itemId, Long ownerId) {
        Item item = getItem(itemId);
        checkItemOwner(item, ownerId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    private void checkItemOwner(Item item, Long ownerId) {
        if (!item.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedModificationException("Изменять\\удалять вещь может только её владелец");
        }
    }
}
