package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemCreateDto itemCreateDto, Long userId) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> updateItem(long itemId, ItemUpdateDto itemUpdateDto, long userId) {
        return patch("/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemsByOwnerId(long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> deleteItem(Long itemId, Long userId) {
        return delete("/items/{itemId}", userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        String path = UriComponentsBuilder.fromPath("/search")
                .queryParam("text", text)
                .toUriString();
        return get(path);
    }

    public ResponseEntity<Object> postComment(CommentCreateDto commentCreateDto, long itemId, long userId) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }
}