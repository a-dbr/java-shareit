package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private void injectItemMapper() {
        try {
            Field f = mapper.getClass().getDeclaredField("itemMapper");
            f.setAccessible(true);
            f.set(mapper, itemMapper);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось инжектировать itemMapper в ItemRequestMapper", e);
        }
    }

    @Test
    void fromCreateDto_null_returnsNull() {
        ItemRequest req = mapper.fromCreateDto(null);
        assertThat(req).isNull();
    }

    @Test
    void fromCreateDto_mapsDescription_and_ignoresIdRequesterCreatedItems() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна дрель");

        ItemRequest result = mapper.fromCreateDto(createDto);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getId()).isNull();
        assertThat(result.getRequester()).isNull();
        assertThat(result.getCreated()).isNull();
        assertThat(result.getItems()).isNullOrEmpty();
    }


    @Test
    void toItemRequestDto_null_returnsNull() {
        ItemRequestDto dto = mapper.toItemRequestDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toItemRequestDto_mapsRequesterId_and_items() {
        injectItemMapper();

        ItemRequest request = new ItemRequest();
        request.setId(50L);
        request.setDescription("Пожалуйста, отвертку");
        request.setCreated(Instant.now());

        User requester = new User();
        requester.setId(7L);
        requester.setName("Ivan");
        request.setRequester(requester);

        Item item = new Item();
        item.setId(11L);
        item.setName("Отвертка");
        item.setDescription("Крестовая");
        item.setAvailable(true);

        Set<Item> items = new HashSet<>();
        items.add(item);
        request.setItems(items);

        ItemRequestDto dto = mapper.toItemRequestDto(request);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(50L);
        assertThat(dto.getDescription()).isEqualTo("Пожалуйста, отвертку");
        assertThat(dto.getRequesterId()).isEqualTo(7L);
        assertThat(dto.getCreated()).isEqualTo(request.getCreated());
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().iterator().next().getId()).isEqualTo(11L);
    }

    @Test
    void toItemRequestDto_handlesNullRequester_andNullItems() {
        injectItemMapper();

        ItemRequest request = new ItemRequest();
        request.setId(99L);
        request.setDescription("Без requester и без items");
        request.setCreated(Instant.now());

        ItemRequestDto dto = mapper.toItemRequestDto(request);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getRequesterId()).isNull();
        assertThat(dto.getItems()).isNullOrEmpty();
    }

}
