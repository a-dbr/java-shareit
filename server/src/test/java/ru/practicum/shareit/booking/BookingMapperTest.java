package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.testutil.TestDataFactory;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toDto_MapsAllFieldsCorrectly() {

        User owner = TestDataFactory.user(10L);
        User booker = TestDataFactory.user(20L);
        Item item = TestDataFactory.item(100L, owner);

        Booking booking = TestDataFactory.booking(
                1L,
                item,
                booker,
                TestDataFactory.FIXED_NOW.plusDays(1),
                TestDataFactory.FIXED_NOW.plusDays(2)
        );

        BookingDto dto = mapper.toDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());

        if (dto.getItem() != null) {
            assertThat(dto.getItem().getId()).isEqualTo(item.getId());
            assertThat(dto.getItem().getName()).isEqualTo(item.getName());
        }
        if (dto.getBooker() != null) {
            assertThat(dto.getBooker().getId()).isEqualTo(booker.getId());
            assertThat(dto.getBooker().getName()).isEqualTo(booker.getName());
        }
    }
}
