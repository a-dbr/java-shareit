package ru.practicum.shareit.user;

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
import ru.practicum.shareit.testutil.TestDataFactory;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private static final String USERS_ENDPOINT = "/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private UserCreateDto createDto;
    private UserUpdateDto updateDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = TestDataFactory.user(1L).getId();

        createDto = new UserCreateDto();
        createDto.setName("Alice");
        createDto.setEmail("alice@example.com");

        updateDto = new UserUpdateDto();
        updateDto.setName("Alice Updated");
        updateDto.setEmail("alice.new@example.com");

        userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName(createDto.getName());
        userDto.setEmail(createDto.getEmail());
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(userDto);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).createUser(any(UserCreateDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void createUser_blankEmail_shouldReturnBadRequest() throws Exception {
        UserCreateDto bad = new UserCreateDto();
        bad.setName("NoEmail");
        bad.setEmail("");

        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    void updateUser_shouldReturnUpdated() throws Exception {
        Long id = 5L;

        UserDto updated = new UserDto();
        updated.setId(id);
        updated.setName(updateDto.getName());
        updated.setEmail(updateDto.getEmail());

        when(userService.updateUser(eq(id), any(UserUpdateDto.class))).thenReturn(updated);

        mockMvc.perform(patch(USERS_ENDPOINT + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.name").value(updateDto.getName()))
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()));

        verify(userService, times(1)).updateUser(eq(id), any(UserUpdateDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUser_shouldReturnDto() throws Exception {
        Long id = 7L;
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName("Bob");
        dto.setEmail("bob@example.com");

        when(userService.getUserDto(eq(id))).thenReturn(dto);

        mockMvc.perform(get(USERS_ENDPOINT + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.name").value(dto.getName()));

        verify(userService, times(1)).getUserDto(eq(id));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserDto u1 = new UserDto();
        u1.setId(1L);
        u1.setName("A");
        u1.setEmail("a@example.com");

        when(userService.getAllUsers()).thenReturn(List.of(u1));

        mockMvc.perform(get(USERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUser_shouldCallService() throws Exception {
        Long id = 11L;
        doNothing().when(userService).deleteUser(eq(id));

        mockMvc.perform(delete(USERS_ENDPOINT + "/{id}", id))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(eq(id));
        verifyNoMoreInteractions(userService);
    }
}
