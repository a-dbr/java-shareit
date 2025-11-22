package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.enums.TimeStatus;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(long userId, BookingCreateDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateBooking(long userId, long bookingId, boolean approved) {
        String path = UriComponentsBuilder.fromPath("/{bookingId}")
                .queryParam("approved", approved)
                .buildAndExpand(bookingId)
                .toUriString();

        return patch(path, userId);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsByUser(long userId, TimeStatus state) {
        String path = UriComponentsBuilder.fromPath("")
                .queryParam("state", state.name())
                .toUriString();

        return get(path, userId, null);
    }

    public ResponseEntity<Object> getBookingsByOwner(long userId, TimeStatus state) {
        String path = UriComponentsBuilder.fromPath("/owner")
                .queryParam("state", state.name())
                .toUriString();

        return get(path, userId);
    }

}