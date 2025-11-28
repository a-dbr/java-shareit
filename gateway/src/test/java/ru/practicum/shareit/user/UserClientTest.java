package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(UserClient.class)
@ActiveProfiles("test")
class UserClientTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void contextLoads() {
        assertNotNull(userClient);
        assertNotNull(mockServer);
    }

    @Test
    void createUser_shouldSendPostRequest() {

        UserCreateDto userDto = new UserCreateDto();
        userDto.setName("Biba");
        userDto.setEmail("biba@yandex.ru");

        String expectedResponse = "{\"id\":1,\"name\":\"Biba\",\"email\":\"biba@yandex.ru\"}";

        mockServer.expect(requestTo("http://testserver/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Biba"))
                .andExpect(jsonPath("$.email").value("biba@yandex.ru"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.createUser(userDto);

        mockServer.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateUser_shouldSendPatchRequest() {

        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setName("Boba");
        userDto.setEmail("boba@yandex.ru");

        String expectedResponse = "{\"id\":1,\"name\":\"Boba\",\"email\":\"boba@yandex.ru\"}";

        mockServer.expect(requestTo("http://testserver/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Boba"))
                .andExpect(jsonPath("$.email").value("boba@yandex.ru"))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.updateUser(1L, userDto);

        mockServer.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getUser_shouldSendGetRequest() {

        String expectedResponse = "{\"id\":1,\"name\":\"Biba\",\"email\":\"biba@yandex.ru\"}";

        mockServer.expect(requestTo("http://testserver/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUserById(1L);

        mockServer.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteUser_shouldSendDeleteRequest() {
        mockServer.expect(requestTo("http://testserver/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = userClient.deleteUser(1L);

        mockServer.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}