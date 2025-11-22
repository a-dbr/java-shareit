package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemsByOwnerId(userId);
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable("itemId") Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") Long itemId,
                              @Valid @RequestBody ItemUpdateDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.updateItem(itemId, itemDto, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemCreateDto itemDto) {
        return itemClient.createItem(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable("itemId") Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping(path = "/{itemId}/comment")
    public ResponseEntity<Object> postComment(@PathVariable("itemId") Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody CommentCreateDto commentCreateDto) {
        return itemClient.postComment(commentCreateDto, itemId, userId);
    }
}
