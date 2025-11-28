package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.testutil.TestDataFactory;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private User owner;
    private ItemDto itemDto;
    private ItemCreateDto createDto;
    private ItemUpdateDto updateDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        owner = TestDataFactory.user(2L);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(owner.getId());

        createDto = new ItemCreateDto();
        createDto.setName("New Item");
        createDto.setDescription("New Description");
        createDto.setAvailable(true);
        createDto.setRequestId(null);

        updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Desc");
        updateDto.setAvailable(false);

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Nice item!");
    }

    @Test
    void getItemsByOwnerId_success() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(itemDto.getId());
        dto.setName(itemDto.getName());
        dto.setDescription(itemDto.getDescription());
        dto.setAvailable(itemDto.getAvailable());
        dto.setOwnerId(itemDto.getOwnerId());
        dto.setComments(Collections.emptySet());
        dto.setLastBooking(null);
        dto.setNextBooking(null);

        Mockito.when(itemService.getItemsByOwnerId(owner.getId()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()))
                .andExpect(jsonPath("$[0].name").value(dto.getName()));

        verify(itemService).getItemsByOwnerId(owner.getId());
    }

    @Test
    void getItem_success() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(itemDto.getId());
        dto.setName(itemDto.getName());
        dto.setDescription(itemDto.getDescription());
        dto.setAvailable(itemDto.getAvailable());
        dto.setComments(Collections.emptySet());
        dto.setLastBooking(null);
        dto.setNextBooking(null);

        Mockito.when(itemService.getItemDto(itemDto.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/items/{id}", itemDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()));

        verify(itemService).getItemDto(itemDto.getId());
    }

    @Test
    void updateItem_success() throws Exception {
        ItemDto updated = new ItemDto();
        updated.setId(itemDto.getId());
        updated.setName(updateDto.getName());
        updated.setDescription(updateDto.getDescription());
        updated.setAvailable(updateDto.getAvailable());
        updated.setOwnerId(owner.getId());

        Mockito.when(itemService.updateItem(eq(itemDto.getId()), any(ItemUpdateDto.class), eq(owner.getId())))
                .thenReturn(updated);

        mockMvc.perform(patch("/items/{id}", itemDto.getId())
                        .header(USER_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated.getName()))
                .andExpect(jsonPath("$.available").value(updated.getAvailable()));

        verify(itemService).updateItem(eq(itemDto.getId()), any(ItemUpdateDto.class), eq(owner.getId()));
    }

    @Test
    void createItem_success() throws Exception {
        ItemDto created = new ItemDto();
        created.setId(10L);
        created.setName(createDto.getName());
        created.setDescription(createDto.getDescription());
        created.setAvailable(createDto.getAvailable());
        created.setOwnerId(owner.getId());

        Mockito.when(itemService.createItem(any(ItemCreateDto.class), eq(owner.getId())))
                .thenReturn(created);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value(created.getName()));

        verify(itemService).createItem(any(ItemCreateDto.class), eq(owner.getId()));
    }

    @Test
    void deleteItem_success() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(eq(itemDto.getId()), eq(owner.getId()));

        mockMvc.perform(delete("/items/{id}", itemDto.getId())
                        .header(USER_HEADER, owner.getId()))
                .andExpect(status().isNoContent());

        verify(itemService).deleteItem(eq(itemDto.getId()), eq(owner.getId()));
    }

    @Test
    void searchItems_success() throws Exception {
        Mockito.when(itemService.getItemsByText("test"))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));

        verify(itemService).getItemsByText("test");
    }

    @Test
    void postComment_success() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(100L);
        commentDto.setText(commentCreateDto.getText());
        commentDto.setAuthorName("User Name");

        Mockito.when(itemService.postComment(eq(commentCreateDto), eq(itemDto.getId()), eq(owner.getId())))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", itemDto.getId())
                        .header(USER_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

        verify(itemService).postComment(eq(commentCreateDto), eq(itemDto.getId()), eq(owner.getId()));
    }
}
