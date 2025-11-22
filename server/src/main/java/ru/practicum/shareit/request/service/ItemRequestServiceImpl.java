package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto createDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId  + "не найден."));

        ItemRequest entity = mapper.fromCreateDto(createDto);
        entity.setRequester(user);
        ItemRequest saved = requestRepository.save(entity);

        return mapper.toItemRequestDto(saved);
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId  + "не найден."));

        List<ItemRequest> requests = requestRepository
                .findAllByRequesterIdOrderByCreatedDescWithItems(userId);

        return requests.stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllOtherRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId  + "не найден."));

        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Недопустимые параметры");
        }

        int page = from / size;
        Pageable pageRequest = PageRequest.of(page, size);

        List<ItemRequest> requests = requestRepository
                .findAllByRequesterIdNotWithItems(userId, pageRequest);

        return requests.stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId  + "не найден."));

        ItemRequest request = requestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID: " + requestId + " не найден."));

        ItemRequestDto dto = mapper.toItemRequestDto(request);

        List<ItemShortDto> items = itemRepository.findAllByRequestId(requestId).stream()
                .map(itemMapper::toShortDto)
                .toList();

        dto.setItems(new LinkedHashSet<>(items));

        return dto;
    }
}
