package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    private final UserService userService = mock(UserService.class);

    private final ItemRequestService requestService = mock(ItemRequestService.class);

    private final ItemRepository repository = mock(ItemRepository.class);

    private final BookingRepository bookingRepository = mock(BookingRepository.class);

    private final CommentRepository commentRepository = mock(CommentRepository.class);

    private ItemMapper mapper = mock(ItemMapper.class);

    private final BookingMapper bookingMapper = mock(BookingMapper.class);

    private final CommentMapper commentMapper = mock(CommentMapper.class);

    private ItemService service =
            new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                    commentRepository, mapper, bookingMapper, commentMapper);

    @Test
    void updateNoOwner() {
        when(userService.getUser(anyLong()))
                .thenReturn(User.builder().name("name").email("user@mail").build());
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description").build()));

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.update(1, 1, mock(ItemDto.class));
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
    void searchTextFromNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.searchText(1, "text", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void searchTextSizeNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.searchText(1, "text", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void searchTextSizeZero() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.searchText(1, "text", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void searchTextIsEmpty() {
        List<ItemDto> items = service.searchText(1, "", 0, 1);

        assertEquals(0, items.size(), "Не возвращает пустой список при пустом тексте");
    }

    @Test
    void addCommentBookingIsEmpty() {
        when(bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.empty());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.addComment(1, 1, mock(CommentDto.class));
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getItemByIdUnknown() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.getItem(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getItemByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            service.getItem(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateNameDescriptionAndAvailable() {
        mapper = new ItemMapper();
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        when(repository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                        .owner(User.builder().id(1L).build())
                        .name("name")
                        .description("Desc")
                        .available(false).build()));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(update.getAvailable(), itemDto.getAvailable(), "не сохроняет available");
        assertEquals(update.getDescription(), itemDto.getDescription(), "не сохроняет description");
        assertEquals(update.getName(), itemDto.getName(), "не сохроняет name");

        mapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
    }

    @Test
    void updateName() {
        mapper = new ItemMapper();
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        Item itemRepository = Item.builder()
                .owner(User.builder().id(1L).build())
                .name("name")
                .description("Desc")
                .available(false).build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(itemRepository.isAvailable(), itemDto.getAvailable(), "Изменяет available, но не должен");
        assertEquals(itemRepository.getDescription(), itemDto.getDescription(), "Изменяет description, но не должен");
        assertEquals(update.getName(), itemDto.getName(), "не сохроняет name");

        mapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
    }

    @Test
    void updateDescription() {
        mapper = new ItemMapper();
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        Item itemRepository = Item.builder()
                .owner(User.builder().id(1L).build())
                .name("name")
                .description("Desc")
                .available(false).build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(itemRepository.isAvailable(), itemDto.getAvailable(), "Изменяет available, но не должен");
        assertEquals(update.getDescription(), itemDto.getDescription(), "не сохроняет description");
        assertEquals(itemRepository.getName(), itemDto.getName(), "Изменяет name, но не должен");

        mapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
    }

    @Test
    void updateAvailable() {
        mapper = new ItemMapper();
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
        when(userService.getUser(anyLong())).thenReturn(User.builder().id(1L).build());
        Item itemRepository = Item.builder()
                .owner(User.builder().id(1L).build())
                .name("name")
                .description("Desc")
                .available(false).build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRepository));
        ItemDto update = ItemDto.builder().available(true).description("description").name("Pen").build();

        ItemDto itemDto = service.update(1, 1, update);

        assertNotNull(itemDto, "Null при возвращении (проверить работу маппера)");
        assertEquals(update.getAvailable(), itemDto.getAvailable(), "не сохроняет available");
        assertEquals(itemRepository.getDescription(), itemDto.getDescription(), "Изменяет description, но не должен");
        assertEquals(itemRepository.getName(), itemDto.getName(), "Изменяет name, но не должен");

        mapper = mock(ItemMapper.class);
        service = new ItemServiceImpl(userService, requestService, repository, bookingRepository,
                commentRepository, mapper, bookingMapper, commentMapper);
    }
}