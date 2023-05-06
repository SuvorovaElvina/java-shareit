package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    private final UserRepository repository = new UserRepositoryImpl();
    private final UserService service = new UserServiceImpl(repository);
    private final UserMapper mapper = new UserMapper();

    @BeforeEach
    void beforeEach() {
        service.getAll().clear();
    }

    @Test
    void add() {
        User user = service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, user.getId());
        assertEquals(1, repository.getAll().size());
    }

    @Test
    void addDuplicateEmail() {
        service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        Throwable thrown = assertThrows(DuplicateException.class, () -> {
            service.add(User.builder()
                    .name("name")
                    .email("email@mail.ru").build());
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateAll() {
        User user = service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        user.setEmail("user@user.ru");
        User update = service.update(user.getId(), mapper.toUserDto(user));

        assertEquals(user.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("user@user.ru", update.getEmail());
    }

    @Test
    void updateName() {
        User user = service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setName("Nik");
        User update = service.update(user.getId(), mapper.toUserDto(user));

        assertEquals(user.getId(), update.getId());
        assertEquals("Nik", update.getName());
        assertEquals("email@mail.ru", update.getEmail());
    }

    @Test
    void updateDuplicateEmail() {
        service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        User user = service.add(User.builder()
                .name("name")
                .email("user@mail.ru").build());
        user.setEmail("email@mail.ru");
        Throwable thrown = assertThrows(DuplicateException.class, () -> {
            service.update(user.getId(), mapper.toUserDto(user));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateEmail() {
        User user = service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());
        user.setEmail("user@user.ru");
        User update = service.update(user.getId(), mapper.toUserDto(user));

        assertEquals(user.getId(), update.getId());
        assertEquals("name", update.getName());
        assertEquals("user@user.ru", update.getEmail());
    }

    @Test
    void getById() {
        User user = service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(user, service.getById(user.getId()));
    }

    @Test
    void getByIdNegative() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            service.getById(-1);
        });

        assertNull(thrown.getMessage());
    }

    @Test
    void getByIdUnknown() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            service.getById(999);
        });

        assertNull(thrown.getMessage());
    }

    @Test
    void getAll() {
        assertEquals(0, service.getAll().size());

        User user = service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, service.getAll().size());

        service.delete(user.getId());

        assertEquals(0, service.getAll().size());
    }

    @Test
    void delete() {
        User user = service.add(User.builder()
                .name("name")
                .email("email@mail.ru").build());

        assertEquals(1, repository.getAll().size());

        service.delete(user.getId());

        assertEquals(0, repository.getAll().size());
    }

    @Test
    void deleteByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            service.delete(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void deleteByIdUnknown() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            service.delete(999);
        });

        assertNull(thrown.getMessage());
    }
}