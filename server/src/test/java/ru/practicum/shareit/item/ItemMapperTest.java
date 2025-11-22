package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.testutil.TestDataFactory;

import static org.assertj.core.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void fromCreateDto_mapsAllFieldsCorrectly() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Test Item");
        dto.setDescription("A very nice item");
        dto.setAvailable(true);
        dto.setRequestId(42L);

        Item result = itemMapper.fromCreateDto(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getDescription()).isEqualTo(dto.getDescription());
        assertThat(result.getAvailable()).isEqualTo(dto.getAvailable());

        assertThat(result.getRequest()).isNull();
    }

    @Test
    void toDto_mapsOwnerIdAndOtherFieldsCorrectly() {
        User owner = TestDataFactory.user(10L);

        Item item = new Item();
        item.setId(5L);
        item.setName("Sample Item");
        item.setDescription("Item description");
        item.setAvailable(false);
        item.setOwner(owner);

        ItemDto dto = itemMapper.toDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void updateItem_updatesNonNullFieldsOnly() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Updated Name");

        Item item = new Item();
        item.setId(1L);
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(true);

        Item updatedItem = itemMapper.updateItem(dto, item);

        assertThat(updatedItem).isNotNull();

        assertThat(updatedItem.getName()).isEqualTo(dto.getName());

        assertThat(updatedItem.getDescription()).isNull();
        assertThat(updatedItem.getAvailable()).isNull();
    }

    @Test
    void updateItem_updatesAllFields() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("New Name");
        dto.setDescription("New Description");
        dto.setAvailable(false);

        Item item = new Item();
        item.setId(1L);
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(true);

        Item updatedItem = itemMapper.updateItem(dto, item);

        assertThat(updatedItem.getName()).isEqualTo(dto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(dto.getDescription());
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void toDtoWithBookings_mapsAllFieldsExceptCommentsAndBookings() {
        Item item = new Item();
        item.setId(3L);
        item.setName("Booking Item");
        item.setDescription("Desc");
        item.setAvailable(true);

        ItemWithBookingsDto dto = itemMapper.toDtoWithBookings(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void toShortDto_mapsIdNameOwnerIdCorrectly() {
        User owner = TestDataFactory.user(7L);

        Item item = new Item();
        item.setId(8L);
        item.setName("Short Item");
        item.setOwner(owner);

        ItemShortDto dto = itemMapper.toShortDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
    }
}
