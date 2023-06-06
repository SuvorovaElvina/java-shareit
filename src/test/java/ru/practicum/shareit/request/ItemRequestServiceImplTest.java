package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.reposiroty.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {
    private final UserService userService = mock(UserService.class);

    private final ItemRequestRepository repository = mock(ItemRequestRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final ItemRequestMapper mapper = mock(ItemRequestMapper.class);

    private final ItemMapper itemMapper = mock(ItemMapper.class);

    private final ItemRequestService service =
            new ItemRequestServiceImpl(userService, repository, itemRepository, mapper, itemMapper);

    @Test
    void getAllByUserFromNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByUser(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByUser(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeZero() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByUser(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllFromNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAll(1, -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAll(1, 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllSizeZero() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAll(1, 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.reply(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void replyGetRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .description("desc")
                .id(1L)
                .created(LocalDateTime.now())
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest request = service.reply(1);

        assertEquals(itemRequest.getId(), request.getId(), "Не возвращает id");
        assertEquals(itemRequest.getCreated(), request.getCreated(), "Не возвращает created");
        assertEquals(itemRequest.getDescription(), request.getDescription(), "Не возвращает description");
    }

}