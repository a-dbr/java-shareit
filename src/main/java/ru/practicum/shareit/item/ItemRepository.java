package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item findById(Long id);

    List<Item> findAllByOwnerId(Long ownerId);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Item> findByText(String text);
}
