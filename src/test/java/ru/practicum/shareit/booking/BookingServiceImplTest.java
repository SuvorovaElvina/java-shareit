package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    private BookingMapper mapper = mock(BookingMapper.class);

    private final ItemService itemService = mock(ItemService.class);

    private final UserService userService = mock(UserService.class);

    private final BookingRepository repository = mock(BookingRepository.class);

    private BookingService service = new BookingServiceImpl(mapper, itemService, userService, repository);

    private final BookingDto bookingDto = BookingDto.builder().end(LocalDateTime.now()).start(LocalDateTime.now()).itemId(1L).build();

    @Test
    void createExceptionWhenAvailableIsFalse() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .available(false)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void createExceptionWhenOwner() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTimeExceptionEndBeforeStart() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now().minusDays(1))
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTimeExceptionEndEqualsStart() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now())
                        .start(LocalDateTime.now())
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void validateTimeExceptionStartBeforeNow() {
        when(itemService.getItem(anyLong()))
                .thenReturn(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build());
        when(mapper.toBooking(any()))
                .thenReturn(Booking.builder()
                        .end(LocalDateTime.now().plusHours(1))
                        .start(LocalDateTime.now().minusDays(1))
                        .build());

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.create(1, bookingDto);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionUnknown() {
       when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.getById(1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionNegative() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            service.getById(1, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getBookingExceptionNoOwnerItemAndNoBooker() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(Booking.builder()
                .booker(User.builder().id(2L).email("user@mail").name("name").build())
                .item(Item.builder()
                        .owner(User.builder().id(2L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now().plusHours(1))
                .build()));
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.getById(1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserFromNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByUser(1, "ALL", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByUser(1, "ALL", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserSizeZero() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByUser(1, "ALL", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByUserStateUnknown() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            service.getAllByUser(1, "qwq", 0, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerFromNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByOwner(1, "ALL", -1, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerSizeNegative() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByOwner(1, "ALL", 0, -1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerSizeZero() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.getAllByOwner(1, "ALL", 0, 0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getAllByOwnerStateUnknown() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            service.getAllByOwner(1, "qwq", 0, 1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateApprovedIsNull() {
        Throwable thrown = assertThrows(UnknownStateException.class, () -> {
            service.update(1, 1, null);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateNoOwner() {
        when(repository.findById(any()))
                .thenReturn(Optional.of(Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .item(Item.builder()
                                .owner(User.builder().id(2L).name("name").email("user@mail").build())
                                .available(true)
                                .name("name")
                                .description("description")
                                .build())
                        .build()));
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateStatusApproved() {
        when(repository.findById(any()))
                .thenReturn(Optional.of(Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .item(Item.builder()
                                .owner(User.builder().id(1L).name("name").email("user@mail").build())
                                .available(true)
                                .name("name")
                                .description("description")
                                .build())
                        .status(Status.APPROVED)
                        .build()));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void updateStatusRejected() {
        when(repository.findById(any()))
                .thenReturn(Optional.of(Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .item(Item.builder()
                                .owner(User.builder().id(1L).name("name").email("user@mail").build())
                                .available(true)
                                .name("name")
                                .description("description")
                                .build())
                        .status(Status.REJECTED)
                        .build()));
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            service.update(1, 1, false);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getByIdWhenOwnerItemWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        Booking booking = Booking.builder()
                .id(1L)
                .status(Status.WAITING)
                .booker(User.builder().id(2L).name("name").email("user@mail").build())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.getById(1, 1);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void getByIdWhenBookerWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        Booking booking = Booking.builder()
                .booker(User.builder().id(2L).name("name").email("user@mail").build())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.getById(2, 1);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void updateWhenApprovedTrueWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        Booking booking = Booking.builder()
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.update(1, 1, true);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");
        assertEquals(Status.APPROVED, bookingDto.getStatus(), "Не возвращает нужный status");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

    @Test
    void updateWhenApprovedFalseWithMapper() {
        mapper = new BookingMapper();
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
        Booking booking = Booking.builder()
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(Item.builder()
                        .owner(User.builder().id(1L).name("name").email("user@mail").build())
                        .available(true)
                        .name("name")
                        .description("description")
                        .build())
                .build();
        when(repository.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.update(1, 1, false);

        assertNotNull(bookingDto, "null при получении (возможно проверить работу маппера)");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Не возвращается нужный start");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Не возвращает нужный end");
        assertEquals(booking.getItem(), bookingDto.getItem(), "Не возвращает нужный item");
        assertEquals(Status.REJECTED, bookingDto.getStatus(), "Не возвращает нужный status");

        mapper = mock(BookingMapper.class);
        service = new BookingServiceImpl(mapper, itemService, userService, repository);
    }

}