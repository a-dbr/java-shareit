package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createUser_shouldReturnOk() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Biba");
        userCreateDto.setEmail("biba@boba.com");

        when(userClient.createUser(any())).thenReturn(ResponseEntity.ok(userCreateDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userCreateDto.getName()))
                .andExpect(jsonPath("$.email").value(userCreateDto.getEmail()));

        verify(userClient).createUser(any());
    }

    @Test
    void createUser_shouldReturnBadRequest_whenInvalidData() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto();
        invalidDto.setName("");
        invalidDto.setEmail("not_valid_email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Biba");
        userUpdateDto.setEmail("biba@boba.com");
        when(userClient.updateUser(eq(1L), any())).thenReturn(ResponseEntity.ok(userUpdateDto));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userUpdateDto.getEmail()));

        verify(userClient).updateUser(eq(1L), any());
    }

    @Test
    void getUser_shouldReturnOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Biba");
        userDto.setEmail("biba@boba.com");
        when(userClient.getUserById(1L)).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.getName()));

        verify(userClient).getUserById(1L);
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        when(userClient.deleteUser(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(1L);
    }
}