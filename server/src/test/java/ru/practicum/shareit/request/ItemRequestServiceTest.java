package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.testutil.TestDataFactory;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    public static final Instant FIXED_NOW = Instant.now();

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper mapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User requester;
    private ItemRequest requestEntity;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requester = TestDataFactory.user(1L);

        requestEntity = new ItemRequest();
        requestEntity.setId(10L);
        requestEntity.setDescription("Нужна дрель");
        requestEntity.setRequester(requester);
        requestEntity.setCreated(FIXED_NOW);

        requestDto = new ItemRequestDto();
        requestDto.setId(requestEntity.getId());
        requestDto.setDescription(requestEntity.getDescription());
        requestDto.setRequesterId(requester.getId());
        requestDto.setCreated(FIXED_NOW);
        requestDto.setItems(Set.of());
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        Long userId = requester.getId();
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна дрель");

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(requester));
        when(mapper.fromCreateDto(eq(createDto))).thenReturn(requestEntity);
        when(requestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest arg = invocation.getArgument(0);
            arg.setId(requestEntity.getId());
            return arg;
        });
        when(mapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(requestDto);

        ItemRequestDto result = service.create(userId, createDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(requestEntity.getId());
        assertThat(result.getDescription()).isEqualTo(createDto.getDescription());

        verify(userRepository).findById(eq(userId));
        verify(mapper).fromCreateDto(eq(createDto));
        verify(requestRepository).save(any(ItemRequest.class));
        verify(mapper).toItemRequestDto(any(ItemRequest.class));
        verifyNoMoreInteractions(userRepository, mapper, requestRepository, itemRepository, itemMapper);
    }

    @Test
    void create_userNotFound_shouldThrowNotFound() {
        Long userId = 999L;
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Мне нужен шуруповерт");

        when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(userId, createDto));

        verify(userRepository).findById(eq(userId));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getOwnRequests_shouldReturnMappedList() {
        Long userId = requester.getId();

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(requester));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDescWithItems(eq(userId)))
                .thenReturn(List.of(requestEntity));
        when(mapper.toItemRequestDto(eq(requestEntity))).thenReturn(requestDto);

        List<ItemRequestDto> result = service.getOwnRequests(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(requestDto.getId());

        verify(userRepository).findById(eq(userId));
        verify(requestRepository).findAllByRequesterIdOrderByCreatedDescWithItems(eq(userId));
        verify(mapper).toItemRequestDto(eq(requestEntity));
    }

    @Test
    void getAllOtherRequests_withPagination_shouldReturnList() {
        Long userId = requester.getId();
        int from = 0;
        int size = 10;

        ItemRequest other = new ItemRequest();
        other.setId(20L);
        other.setDescription("Запрос");
        User otherUser = TestDataFactory.user(2L);
        other.setRequester(otherUser);
        other.setCreated(FIXED_NOW);

        ItemRequestDto otherDto = new ItemRequestDto();
        otherDto.setId(other.getId());
        otherDto.setDescription(other.getDescription());
        otherDto.setRequesterId(otherUser.getId());
        otherDto.setCreated(FIXED_NOW);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(requester));
        when(requestRepository.findAllByRequesterIdNotWithItems(eq(userId), any()))
                .thenReturn(List.of(other));
        when(mapper.toItemRequestDto(eq(other))).thenReturn(otherDto);

        List<ItemRequestDto> result = service.getAllOtherRequests(userId, from, size);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(otherDto.getId());

        verify(userRepository).findById(eq(userId));
        verify(requestRepository).findAllByRequesterIdNotWithItems(eq(userId), any());
        verify(mapper).toItemRequestDto(eq(other));
    }

    @Test
    void getAllOtherRequests_invalidPagination_shouldThrowIllegalArgument() {
        Long userId = requester.getId();
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(requester));

        assertThrows(IllegalArgumentException.class, () -> service.getAllOtherRequests(userId, -1, 5));
        assertThrows(IllegalArgumentException.class, () -> service.getAllOtherRequests(userId, 0, 0));
    }

    @Test
    void getById_shouldReturnDto_whenFound() {
        Long userId = requester.getId();
        Long requestId = requestEntity.getId();

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(requester));
        when(requestRepository.findByIdWithItems(eq(requestId))).thenReturn(Optional.of(requestEntity));
        when(mapper.toItemRequestDto(eq(requestEntity))).thenReturn(requestDto);
        when(itemRepository.findAllByRequestId(eq(requestId))).thenReturn(List.of());

        ItemRequestDto result = service.getById(userId, requestId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(requestDto.getId());

        verify(userRepository).findById(eq(userId));
        verify(requestRepository).findByIdWithItems(eq(requestId));
        verify(mapper).toItemRequestDto(eq(requestEntity));
        verify(itemRepository).findAllByRequestId(eq(requestId));
    }

    @Test
    void getById_requestNotFound_shouldThrowNotFound() {
        Long userId = requester.getId();
        Long requestId = 999L;

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(requester));
        when(requestRepository.findByIdWithItems(eq(requestId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(userId, requestId));

        verify(requestRepository).findByIdWithItems(eq(requestId));
    }
}
