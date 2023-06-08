package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository repository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;
    private final User user = User.builder().id(1L).name("name").email("user@mail").build();
    private final Item item = Item.builder().id(1L).owner(user).name("name").description("desc").build();

    @AfterEach
    void setUp() {
        repository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByOwnerIdAndStatusIn() {
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdAndStatusIn(1L, Set.of(Status.WAITING, Status.APPROVED),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByOwnerIdAndStatus() {
        user.setId(2L);
        item.setId(2L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdAndStatus(2L, Status.REJECTED,
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByOwnerId() {
        user.setId(4L);
        item.setOwner(user);
        item.setId(4L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerId(4L, PageRequest.of(0, 2))
                .stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(2, bookings.size(), "Не возвращает список с 2");
        assertEquals(booking, bookings.get(0), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(1), "Не возвращает список с 2");
    }

    @Test
    void findByOwnerIdCurrent() {
        user.setId(5L);
        item.setId(5L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now().minusDays(1)).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdCurrent(5L, LocalDateTime.now(),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByOwnerIdPast() {
        user.setId(3L);
        item.setOwner(user);
        item.setId(3L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdPast(3L, LocalDateTime.now(),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }
}