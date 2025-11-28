package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.TimeStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.testutil.TestDataFactory;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = TestDataFactory.bookingDto(
                0L,
                TestDataFactory.FIXED_NOW.plusDays(1),
                TestDataFactory.FIXED_NOW.plusDays(2)
        );
    }

    @Test
    void createBooking_ReturnsCreated() throws Exception {
        BookingDto returned = TestDataFactory.bookingDto(101L,
                bookingDto.getStart(), bookingDto.getEnd());
        Mockito.when(bookingService.createBooking(any(BookingCreateDto.class), eq(2L)))
                .thenReturn(returned);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header(USER_HEADER, 2L))
                .andExpect(status().isCreated());
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingService.approveBooking(anyLong(), anyLong(), eq(true)))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getBookingDto(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getUserBookings_shouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookingByBookerIdAndStatus(anyLong(), any(TimeStatus.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 2)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void getOwnerBookings_shouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookingByOwnerId(anyLong(), any(TimeStatus.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }
}
