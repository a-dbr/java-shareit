package ru.practicum.shareit.user;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User fromDto(@NotNull UserCreateDto userDto);

    UserDto toDto(@NotNull User user);
}