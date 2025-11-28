package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void fromDto_shouldMapNameAndEmail() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Biba");
        createDto.setEmail("biba@example.com");

        User user = mapper.fromDto(createDto);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(createDto.getName());
        assertThat(user.getEmail()).isEqualTo(createDto.getEmail());
    }

    @Test
    void fromDto_null_returnsNull() {
        User user = mapper.fromDto(null);
        assertThat(user).isNull();
    }

    @Test
    void toDto_shouldMapAllFields_whenUserPresent() {
        User user = new User();
        user.setId(42L);
        user.setName("Boba");
        user.setEmail("boba@example.com");

        UserDto dto = mapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void toDto_null_returnsNull() {
        UserDto dto = mapper.toDto(null);
        assertThat(dto).isNull();
    }
}