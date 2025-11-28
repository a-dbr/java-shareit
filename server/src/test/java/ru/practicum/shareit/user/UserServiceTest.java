package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateDto createDto;
    private User userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        createDto = new UserCreateDto();
        createDto.setName("Tester");
        createDto.setEmail("tester@example.com");

        userEntity = new User();
        userEntity.setId(10L);
        userEntity.setName(createDto.getName());
        userEntity.setEmail(createDto.getEmail());

        userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setName(userEntity.getName());
        userDto.setEmail(userEntity.getEmail());
    }

    @Test
    void createUser_whenValid_thenReturnsDto() {
        when(userRepository.existsByEmail(createDto.getEmail())).thenReturn(false);

        User entityFromDto = new User();
        entityFromDto.setName(createDto.getName());
        entityFromDto.setEmail(createDto.getEmail());

        when(userMapper.fromDto(eq(createDto))).thenReturn(entityFromDto);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(userEntity.getId());
            return u;
        });
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.createUser(createDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userEntity.getId());
        assertThat(result.getEmail()).isEqualTo(createDto.getEmail());

        verify(userRepository, times(1)).existsByEmail(createDto.getEmail());
        verify(userMapper, times(1)).fromDto(eq(createDto));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(any(User.class));
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createUser_whenEmailNull_thenThrowsIllegalArgument() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("NoEmail");
        dto.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));

        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createUser_whenEmailAlreadyUsed_thenThrows() {
        when(userRepository.existsByEmail(createDto.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.createUser(createDto));

        verify(userRepository, times(1)).existsByEmail(createDto.getEmail());
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_whenExistsAndEmailFree_thenUpdates() {
        Long id = 20L;
        User existing = new User();
        existing.setId(id);
        existing.setName("Old");
        existing.setEmail("old@example.com");

        UserUpdateDto upd = new UserUpdateDto();
        upd.setName("NewName");
        upd.setEmail("new@example.com");

        when(userRepository.findById(eq(id))).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail(eq(upd.getEmail()))).thenReturn(false);
        when(userRepository.save(eq(existing))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setName(upd.getName());
            u.setEmail(upd.getEmail());
            return u;
        });

        UserDto mapped = new UserDto();
        mapped.setId(id);
        mapped.setName(upd.getName());
        mapped.setEmail(upd.getEmail());
        when(userMapper.toDto(eq(existing))).thenReturn(mapped);

        UserDto res = userService.updateUser(id, upd);

        assertThat(res).isNotNull();
        assertThat(res.getName()).isEqualTo(upd.getName());
        assertThat(res.getEmail()).isEqualTo(upd.getEmail());

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).existsByEmail(upd.getEmail());
        verify(userRepository, times(1)).save(existing);
        verify(userMapper, times(1)).toDto(existing);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_whenNotFound_thenThrows() {
        Long id = 999L;
        when(userRepository.findById(eq(id))).thenReturn(Optional.empty());

        UserUpdateDto upd = new UserUpdateDto();
        upd.setName("Any");

        assertThrows(NotFoundException.class, () -> userService.updateUser(id, upd));

        verify(userRepository, times(1)).findById(id);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_whenEmailUsedByOther_thenThrows() {
        Long id = 30L;
        User existing = new User();
        existing.setId(id);
        existing.setEmail("old@example.com");

        UserUpdateDto upd = new UserUpdateDto();
        upd.setEmail("already@used.com");

        when(userRepository.findById(eq(id))).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail(eq(upd.getEmail()))).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.updateUser(id, upd));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).existsByEmail(upd.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getUserDto_whenFound_thenReturnsDto() {
        Long id = 40L;
        User u = new User();
        u.setId(id);
        u.setName("U");
        u.setEmail("u@example.com");

        when(userRepository.findById(eq(id))).thenReturn(Optional.of(u));

        UserDto mapped = new UserDto();
        mapped.setId(id);
        mapped.setName("U");
        mapped.setEmail("u@example.com");
        when(userMapper.toDto(eq(u))).thenReturn(mapped);

        UserDto dto = userService.getUserDto(id);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);

        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toDto(u);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getUser_whenNotFound_thenThrows() {
        when(userRepository.findById(777L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUser(777L));
        verify(userRepository, times(1)).findById(777L);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getAllUsers_returnsMappedList() {
        User u = new User();
        u.setId(1L);
        u.setName("A");
        u.setEmail("a@example.com");

        when(userRepository.findAll()).thenReturn(List.of(u));

        UserDto mapped = new UserDto();
        mapped.setId(1L);
        mapped.setName("A");
        mapped.setEmail("a@example.com");
        when(userMapper.toDto(eq(u))).thenReturn(mapped);

        List<UserDto> all = userService.getAllUsers();

        assertThat(all).hasSize(1);
        assertThat(all.getFirst().getId()).isEqualTo(1L);

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(u);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void deleteUser_callsRepository() {
        Long id = 5L;
        doNothing().when(userRepository).deleteById(eq(id));

        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void isUserExist_checksRepository() {
        when(userRepository.existsById(15L)).thenReturn(true);
        assertThat(userService.isUserExist(15L)).isTrue();
        verify(userRepository, times(1)).existsById(15L);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}
