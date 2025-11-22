package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.testutil.TestDataFactory;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    public static final Instant FIXED_NOW = Instant.now();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private ItemRequestCreateDto createDto;
    private ItemRequestDto responseDto;

    @BeforeEach
    void setUp() {
        userId = TestDataFactory.user(1L).getId();

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна дрель");

        responseDto = new ItemRequestDto();
        responseDto.setId(100L);
        responseDto.setDescription(createDto.getDescription());
        responseDto.setRequesterId(userId);
        responseDto.setCreated(FIXED_NOW);
        responseDto.setItems(Set.of());
    }

    @Test
    void createRequest_shouldReturnCreated() throws Exception {
        when(itemRequestService.create(eq(userId), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(userId.intValue()));

        verify(itemRequestService, times(1)).create(eq(userId), any(ItemRequestCreateDto.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void createRequest_blankDescription_shouldReturnBadRequest() throws Exception {
        ItemRequestCreateDto badDto = new ItemRequestCreateDto();
        badDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(anyLong(), any());
    }

    @Test
    void getOwnRequests_shouldReturnList() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(11L);
        dto.setDescription("Мне нужен шуруповерт");
        dto.setRequesterId(2L);
        dto.setCreated(FIXED_NOW);

        when(itemRequestService.getOwnRequests(eq(userId))).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(11));

        verify(itemRequestService).getOwnRequests(eq(userId));
    }

    @Test
    void getAllOtherRequests_withPagination_shouldReturnList() throws Exception {
        Long userId2 = TestDataFactory.user(3L).getId();
        int from = 0;
        int size = 5;

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(21L);
        dto.setDescription("Ищу лампу");
        dto.setRequesterId(42L);
        dto.setCreated(FIXED_NOW);

        when(itemRequestService.getAllOtherRequests(eq(userId2), eq(from), eq(size))).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, userId2)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(21));

        verify(itemRequestService).getAllOtherRequests(eq(userId2), eq(from), eq(size));
    }

    @Test
    void getRequestById_shouldReturnDto() throws Exception {
        Long userId3 = TestDataFactory.user(4L).getId();
        Long requestId = 55L;

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(requestId);
        dto.setDescription("Нужна колонка");
        dto.setRequesterId(userId3);
        dto.setCreated(FIXED_NOW);

        when(itemRequestService.getById(eq(userId3), eq(requestId))).thenReturn(dto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_HEADER, userId3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId.intValue()))
                .andExpect(jsonPath("$.description").value(dto.getDescription()));

        verify(itemRequestService).getById(eq(userId3), eq(requestId));
    }
}
