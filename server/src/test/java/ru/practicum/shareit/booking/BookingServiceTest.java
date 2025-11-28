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
import ru.practicum.shareit.booking.enums.TimeStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.testutil.TestDataFactory;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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
    private Booking booking;

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

        booking = TestDataFactory.booking(
                100L,
                item,
                booker,
                bookingDto.getStart(),
                bookingDto.getEnd()
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

    @Test
    void createBooking_shouldThrow_whenItemNotAvailable() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(TestDataFactory.FIXED_NOW.plusDays(1));
        dto.setEnd(TestDataFactory.FIXED_NOW.plusDays(2));

        when(userService.getUser(anyLong())).thenReturn(booker);
        Item notAvailable = new Item();
        notAvailable.setId(item.getId());
        notAvailable.setAvailable(false);
        when(itemService.getItem(anyLong())).thenReturn(notAvailable);

        assertThatThrownBy(() -> bookingService.createBooking(dto, booker.getId()))
                .isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void createBooking_shouldThrow_whenStartAfterEnd() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(TestDataFactory.FIXED_NOW.plusDays(5));
        dto.setEnd(TestDataFactory.FIXED_NOW.plusDays(2));

        when(userService.getUser(anyLong())).thenReturn(booker);
        Item available = new Item();
        available.setId(item.getId());
        available.setAvailable(true);
        when(itemService.getItem(anyLong())).thenReturn(available);

        assertThatThrownBy(() -> bookingService.createBooking(dto, booker.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Дата начала бронирования не может быть позже даты окончания");
    }

    @Test
    void approveBooking_shouldSetApproved_whenOwnerApproves() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertThat(result).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_shouldSetRejected_whenOwnerRejects() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), false);

        assertThat(result).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_shouldThrow_whenNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Long notOwnerId = 999L;

        assertThatThrownBy(() -> bookingService.approveBooking(booking.getId(), notOwnerId, true))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getBookingDto_shouldReturn_whenBookerOrOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemService.checkItemOwner(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long itemIdArg = invocation.getArgument(0);
            Long userIdArg = invocation.getArgument(1);
            return itemIdArg != null && userIdArg != null
                    && itemIdArg.equals(item.getId())
                    && userIdArg.equals(owner.getId());
        });

        BookingDto asBooker = bookingService.getBookingDto(booking.getId(), booker.getId());
        assertThat(asBooker).isNotNull();

        BookingDto asOwner = bookingService.getBookingDto(booking.getId(), owner.getId());
        assertThat(asOwner).isNotNull();
    }


    @Test
    void getBookingDto_shouldThrow_whenNotBookerNorOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Long outsider = 555L;

        assertThatThrownBy(() -> bookingService.getBookingDto(booking.getId(), outsider))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getBookingByBookerIdAndStatus_all_current_past() {
        when(userService.isUserExist(booker.getId())).thenReturn(true);

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(eq(booker.getId())))
                .thenReturn(List.of(booking));
        assertThat(bookingService.getBookingByBookerIdAndStatus(booker.getId(), TimeStatus.ALL)).hasSize(1);

        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        when(bookingRepository.findByBookerIdAndCurrentTime(eq(booker.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        assertThat(bookingService.getBookingByBookerIdAndStatus(booker.getId(), TimeStatus.CURRENT)).hasSize(1);

        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByEndDesc(eq(booker.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        assertThat(bookingService.getBookingByBookerIdAndStatus(booker.getId(), TimeStatus.PAST)).hasSize(1);
    }

    @Test
    void getBookingByOwnerId_statusBranches() {
        when(userService.isUserExist(owner.getId())).thenReturn(true);

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(owner.getId())))
                .thenReturn(List.of(booking));
        assertThat(bookingService.getBookingByOwnerId(owner.getId(), TimeStatus.ALL)).hasSize(1);

        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        assertThat(bookingService.getBookingByOwnerId(owner.getId(), TimeStatus.FUTURE)).hasSize(1);
    }

    @Test
    void getBookingByBookerIdAndStatus_shouldThrow_whenUserNotFound() {
        when(userService.isUserExist(booker.getId())).thenReturn(false);

        assertThatThrownBy(() ->
                bookingService.getBookingByBookerIdAndStatus(booker.getId(), TimeStatus.ALL)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingByOwnerId_shouldThrow_whenOwnerNotFound() {
        when(userService.isUserExist(owner.getId())).thenReturn(false);

        assertThatThrownBy(() ->
                bookingService.getBookingByOwnerId(owner.getId(), TimeStatus.ALL)
        ).isInstanceOf(NotFoundException.class);
    }

}
