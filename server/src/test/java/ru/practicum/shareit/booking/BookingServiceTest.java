package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.testutil.TestDataFactory;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private BookingMapper bookingMapper;

    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingMapper = Mappers.getMapper(BookingMapper.class);
        bookingService = new BookingServiceImpl(itemService, userService, bookingRepository, bookingMapper);

        owner = TestDataFactory.user(1L);
        booker = TestDataFactory.user(2L);
        item = TestDataFactory.item(10L, owner);

        bookingDto = TestDataFactory.bookingDto(
                0L,
                TestDataFactory.FIXED_NOW.plusDays(1),
                TestDataFactory.FIXED_NOW.plusDays(2)
        );
    }

    private void stubValidUserAndItem() {
        when(userService.getUser(anyLong())).thenReturn(booker);
        when(itemService.getItem(anyLong())).thenReturn(item);
    }

    @Test
    void createBooking_Success() {

        stubValidUserAndItem();

        Booking saved = TestDataFactory.booking(100L, item, booker,
                bookingDto.getStart(), bookingDto.getEnd());
        when(bookingRepository.save(any(Booking.class))).thenReturn(saved);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(bookingDto.getStart());
        bookingCreateDto.setEnd(bookingDto.getEnd());
        bookingCreateDto.setItemId(item.getId());

        BookingDto result = bookingService.createBooking(bookingCreateDto, booker.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getStart()).isEqualTo(saved.getStart());
        assertThat(result.getEnd()).isEqualTo(saved.getEnd());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBooking_WhenRepositoryReturnsBooking_StatusChanged() {
        Booking existing = TestDataFactory.booking(200L, item, booker,
                TestDataFactory.FIXED_NOW.plusDays(3), TestDataFactory.FIXED_NOW.plusDays(4));

        when(bookingRepository.findById(existing.getId()))
                .thenReturn(Optional.of(existing));

        bookingService.approveBooking(existing.getId(), owner.getId(), true);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository, times(1)).save(captor.capture());

        Booking saved = captor.getValue();
        assertThat(saved).isNotNull();

        assertThat(saved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

}
