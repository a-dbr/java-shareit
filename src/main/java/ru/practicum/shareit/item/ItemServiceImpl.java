package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingQueryService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingQueryService bookingQueryService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemWithBookingsDto getItemDto(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена"));
        Set<CommentDto> comments = commentRepository.findByItem(item).stream()
                .map(commentMapper::toDto).collect(Collectors.toSet());
        ItemWithBookingsDto itemDto = itemMapper.toDtoWithBookings(item);
        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена"));
    }

    @Override
    public List<ItemWithBookingsDto> getItemsByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(item -> {
                    ItemWithBookingsDto itemsDto = itemMapper.toDtoWithBookings(item);
                    BookingDto last = bookingQueryService.getLastBookingByItemId(item.getId());
                    BookingDto next = bookingQueryService.getNextBookingByItemId(item.getId());
                    Set<CommentDto> comments = commentRepository.findByItem(item).stream()
                            .map(commentMapper::toDto).collect(Collectors.toSet());
                    itemsDto.setComments(comments);
                    itemsDto.setLastBooking(last);
                    itemsDto.setNextBooking(next);
                    return itemsDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemCreateDto itemDto, Long ownerId) {
        User owner = userService.getUser(ownerId);
        Item item = itemMapper.fromCreateDto(itemDto);
        item.setOwner(owner);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        if (!checkItemOwner(itemId, ownerId)) {
            throw new AccessDeniedException("Изменять вещь может только её владелец");
        }
        Item item = getItem(itemId);

        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long ownerId) {
        if (checkItemOwner(itemId, ownerId)) {
            itemRepository.deleteById(itemId);
        } else {
            throw new AccessDeniedException("Удалять вещь может только её владелец");
        }
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto postComment(CommentCreateDto commentCreateDto, Long itemId, Long userId) {
        validateCommentCreate(itemId, userId);

        Comment comment = commentMapper.fromCreateDto(commentCreateDto);
        comment.setAuthor(userService.getUser(userId));
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена")));

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public boolean checkItemOwner(Long itemId, Long ownerId) {
        return itemRepository.findById(itemId)
                .map(Item::getOwner)
                .map(User::getId)
                .filter(id -> id.equals(ownerId))
                .isPresent();
    }

    @Override
    public boolean existById(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    private void validateCommentCreate(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Не найдено вещи c ID = %d".formatted(itemId));
        }
        if (!userService.isUserExist(userId)) {
            throw new NotFoundException("Не найдено пользователя c ID = %d".formatted(userId));
        }

        if (!bookingQueryService.isBooked(itemId, userId)) {
            throw new IllegalArgumentException("Не найдено бронирований вещи ID = %d пользователем ID = %d"
                    .formatted(itemId, userId));
        }
    }
}
