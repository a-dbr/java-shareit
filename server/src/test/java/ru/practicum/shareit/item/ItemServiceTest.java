package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.testutil.TestDataFactory;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User otherUser;
    private Item item;

    private final Instant fixedInstant = TestDataFactory.FIXED_NOW.atZone(ZoneId.systemDefault()).toInstant();


    @BeforeEach
    void setUp() {
        owner = TestDataFactory.user(1L);
        otherUser = TestDataFactory.user(2L);
        item = TestDataFactory.item(10L, owner);
    }

    @Test
    void getItemDto_shouldReturnDtoWithCommentsAndBookings() {
        Long itemId = item.getId();

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Nice!");
        comment.setAuthor(otherUser);
        comment.setCreated(fixedInstant);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(otherUser.getName());
        commentDto.setCreated(comment.getCreated());

        ItemWithBookingsDto mappedDto = new ItemWithBookingsDto();
        mappedDto.setId(itemId);
        mappedDto.setName(item.getName());
        mappedDto.setDescription(item.getDescription());
        mappedDto.setAvailable(item.getAvailable());
        mappedDto.setOwnerId(owner.getId());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItem(item)).thenReturn(Set.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(itemMapper.toDtoWithBookings(item)).thenReturn(mappedDto);

        ItemWithBookingsDto result = itemService.getItemDto(itemId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().iterator().next().getAuthorName()).isEqualTo(otherUser.getName());

        verify(itemRepository).findById(itemId);
        verify(commentRepository).findByItem(item);
        verify(itemMapper).toDtoWithBookings(item);
    }

    @Test
    void getItem_shouldReturnItem_whenExists() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Item found = itemService.getItem(item.getId());

        assertThat(found).isEqualTo(item);
        verify(itemRepository).findById(item.getId());
    }

    @Test
    void getItem_shouldThrowNotFound_whenMissing() {
        Long missingId = 999L;
        when(itemRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(missingId));
        verify(itemRepository).findById(missingId);
    }

    @Test
    void getItemsByOwnerId_shouldReturnMappedList() {
        Long ownerId = owner.getId();
        Item another = TestDataFactory.item(11L, owner);

        List<Item> items = List.of(item, another);

        ItemWithBookingsDto dto1 = new ItemWithBookingsDto();
        dto1.setId(item.getId());
        dto1.setOwnerId(owner.getId());
        ItemWithBookingsDto dto2 = new ItemWithBookingsDto();
        dto2.setId(another.getId());
        dto2.setOwnerId(owner.getId());

        when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(items);
        when(itemMapper.toDtoWithBookings(item)).thenReturn(dto1);
        when(itemMapper.toDtoWithBookings(another)).thenReturn(dto2);
        when(commentRepository.findByItem(any(Item.class))).thenReturn(Collections.emptySet());

        when(bookingRepository.findTopByItemIdAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findTopByItemIdAndStartAfterOrderByStartAsc(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(null);

        when(bookingRepository.findTopByItemIdAndEndBeforeOrderByEndDesc(eq(another.getId()), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findTopByItemIdAndStartAfterOrderByStartAsc(eq(another.getId()),
                any(LocalDateTime.class))).thenReturn(null);

        List<ItemWithBookingsDto> result = itemService.getItemsByOwnerId(ownerId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemWithBookingsDto::getId).containsExactlyInAnyOrder(10L, 11L);
        verify(itemRepository).findAllByOwnerId(ownerId);

        verify(bookingRepository, atLeast(0)).findTopByItemIdAndEndBeforeOrderByEndDesc(anyLong(),
                any(LocalDateTime.class));
    }

    @Test
    void createItem_shouldSaveAndReturnDto() {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Дрель");
        createDto.setDescription("описание");
        createDto.setAvailable(true);
        createDto.setRequestId(null);

        Item entityFromDto = new Item();
        entityFromDto.setName(createDto.getName());
        entityFromDto.setDescription(createDto.getDescription());
        entityFromDto.setAvailable(createDto.getAvailable());

        Item saved = new Item();
        saved.setId(123L);
        saved.setName(createDto.getName());
        saved.setDescription(createDto.getDescription());
        saved.setAvailable(createDto.getAvailable());
        saved.setOwner(owner);

        ItemDto outDto = new ItemDto();
        outDto.setId(saved.getId());
        outDto.setName(saved.getName());
        outDto.setDescription(saved.getDescription());
        outDto.setAvailable(saved.getAvailable());
        outDto.setOwnerId(owner.getId());

        when(userService.getUser(owner.getId())).thenReturn(owner);
        when(itemMapper.fromCreateDto(createDto)).thenReturn(entityFromDto);
        when(itemRepository.save(any(Item.class))).thenReturn(saved);
        when(itemMapper.toDto(saved)).thenReturn(outDto);

        ItemDto result = itemService.createItem(createDto, owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(saved.getId());
        verify(userService).getUser(owner.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowAccessDenied_whenNotOwner() {
        Long updaterId = otherUser.getId();
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Новое имя");

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> itemService.updateItem(item.getId(), updateDto, updaterId));
    }

    @Test
    void updateItem_shouldUpdate_whenOwner() {
        Long ownerId = owner.getId();
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Обновленное имя");
        updateDto.setDescription("Обновленное описание");
        updateDto.setAvailable(false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Item saved = new Item();
        saved.setId(item.getId());
        saved.setName(updateDto.getName());
        saved.setDescription(updateDto.getDescription());
        saved.setAvailable(updateDto.getAvailable());
        saved.setOwner(owner);

        ItemDto returnedDto = new ItemDto();
        returnedDto.setId(saved.getId());
        returnedDto.setName(saved.getName());
        returnedDto.setDescription(saved.getDescription());
        returnedDto.setAvailable(saved.getAvailable());
        returnedDto.setOwnerId(owner.getId());

        when(itemRepository.save(any(Item.class))).thenReturn(saved);
        when(itemMapper.toDto(saved)).thenReturn(returnedDto);

        ItemDto result = itemService.updateItem(item.getId(), updateDto, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Обновленное имя");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void deleteItem_shouldDelete_whenOwner() {
        Long ownerId = owner.getId();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        itemService.deleteItem(item.getId(), ownerId);

        verify(itemRepository).deleteById(item.getId());
    }

    @Test
    void deleteItem_shouldThrowAccessDenied_whenNotOwner() {
        Long otherId = otherUser.getId();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> itemService.deleteItem(item.getId(), otherId));
    }

    @Test
    void getItemsByText_shouldReturnEmpty_whenNoMatchesOrBlank() {
        String text = "Дрель";
        when(itemRepository.findByText(text)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getItemsByText(text);

        assertThat(result).isEmpty();
        verify(itemRepository).findByText(text);
    }

    @Test
    void existById_shouldReturnTrueOrFalse() {
        when(itemRepository.existsById(10L)).thenReturn(true);
        when(itemRepository.existsById(999L)).thenReturn(false);

        assertThat(itemService.existById(10L)).isTrue();
        assertThat(itemService.existById(999L)).isFalse();
        verify(itemRepository, times(1)).existsById(10L);
        verify(itemRepository, times(1)).existsById(999L);
    }

    @Test
    void checkItemOwner_shouldReturnTrueWhenOwnerMatches() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThat(itemService.checkItemOwner(item.getId(), owner.getId())).isTrue();
        assertThat(itemService.checkItemOwner(item.getId(), otherUser.getId())).isFalse();
    }
}
