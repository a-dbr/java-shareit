package ru.practicum.shareit.testutil;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

public final class TestDataFactory {

    public static final LocalDateTime FIXED_NOW = LocalDateTime.now();

    private TestDataFactory() {
    }

    public static User user(long id) {
        User u = new User();
        u.setId(id);
        u.setName("User" + id);
        u.setEmail("user" + id + "@example.com");
        return u;
    }

    public static Item item(long id, User owner) {
        Item it = new Item();
        it.setId(id);
        it.setName("Item" + id);
        it.setDescription("Тестовая вещь " + id);
        it.setAvailable(true);
        it.setOwner(owner);
        return it;
    }

    public static Booking booking(long id, Item item, User booker,
                                  LocalDateTime start, LocalDateTime end) {
        Booking b = new Booking();
        b.setId(id);
        b.setItem(item);
        b.setBooker(booker);
        b.setStart(start);
        b.setEnd(end);
        return b;
    }

    public static BookingDto bookingDto(long id, LocalDateTime start, LocalDateTime end) {
        BookingDto d = new BookingDto();
        d.setId(id);
        d.setStart(start);
        d.setEnd(end);
        return d;
    }
}
