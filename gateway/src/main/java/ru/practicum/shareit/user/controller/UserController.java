package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userClient.createUser(userCreateDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive long id,
                                             @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return userClient.updateUser(id, userUpdateDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive long id) {
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Positive long id) {
        userClient.deleteUser(id);
    }

}