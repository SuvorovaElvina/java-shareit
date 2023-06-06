package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class UserServiceImplTest {
    private final UserRepository repository = mock(UserRepository.class);

    private final UserMapper mapper = new UserMapper();

    private final UserService service = new UserServiceImpl(repository, mapper);

    @Test
    void getUserNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            service.getUser(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getUserUnknown() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.getUser(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getUser() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));

        User user = service.getUser(1);

        assertNotNull(user, "Null при получении model");
        assertEquals(userRepository, user, "Не передаёт объект");
    }

    @Test
    void getUserByIdWithMapper() {
        UserDto userDtoCurrent = UserDto.builder()
                .id(1L)
                .email("user@mail")
                .name("name").build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build()));

        UserDto userDto = service.getById(1);

        assertNotNull(userDto, "Null при получении dto");
        assertEquals(userDtoCurrent, userDto, "Не передаёт объект");
    }

    @Test
    void updateNameAndEmailWithMapper() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        User userUpdate = User.builder()
                .id(1L)
                .name("Nik")
                .email("Nikol@mail")
                .build();
        when(repository.save(any())).thenReturn(userUpdate);
        UserDto update = UserDto.builder()
                .name("Nik")
                .email("Nikol@mail")
                .build();

        UserDto userDto = service.update(1, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(update.getName(), userDto.getName(), "Не изменяется имя");
        assertEquals(update.getEmail(), userDto.getEmail(), "Не изменяется почта");
    }

    @Test
    void updateNameWithMapper() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        when(repository.save(any())).thenReturn(User.builder()
                .id(1L)
                .name("Nik")
                .email("user@mail")
                .build());
        UserDto update = UserDto.builder()
                .name("Nik")
                .build();

        UserDto userDto = service.update(1, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(update.getName(), userDto.getName(), "Не изменяется имя");
        assertEquals(userRepository.getEmail(), userDto.getEmail(), "Изменяется почта, хотя не должна");
    }

    @Test
    void updateEmailWithMapper() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        when(repository.save(any())).thenReturn(User.builder()
                .id(1L)
                .name("name")
                .email("Nikol@mail")
                .build());
        UserDto update = UserDto.builder()
                .email("Nikol@mail")
                .build();

        UserDto userDto = service.update(1, update);

        assertNotNull(userDto, "null при получении (посмотреть маппер)");
        assertEquals(userRepository.getName(), userDto.getName(), "Изменяется имя, хотя не должно");
        assertEquals(update.getEmail(), userDto.getEmail(), "Не изменяется почта");
    }
}