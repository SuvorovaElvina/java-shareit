package ru.practicum.shareit.booking;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookingRepositoryTest {
    /*@Autowired
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
    void findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike() {
        user.setId(10L);
        item.setId(9L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item).booker(user)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item).booker(user)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(9L,
                10L, LocalDateTime.now(), Status.WAITING).get();

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findFirst1ByItemIdAndStartBeforeOrderByStartDesc() {
        item.setId(6L);
        item.setOwner(null);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now().minusDays(1)).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();

        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        Booking bookings = repository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(6L,
                LocalDateTime.now()).get();

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(booking1, bookings, "Не возвращает список с 1");
    }

    @Test
    void findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc() {
        item.setId(4L);
        item.setOwner(null);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now().plusDays(1)).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();

        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        Booking bookings = repository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(4L,
                LocalDateTime.now(), Status.REJECTED).get();

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(booking, bookings, "Не возвращает список с 1");
    }

    @Test
    void findByBookerIdAndStatusIn() {
        user.setId(2L);
        Booking booking = Booking.builder().status(Status.WAITING).booker(user)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).booker(user)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByBookerIdAndStatusIn(2L, Set.of(Status.WAITING, Status.APPROVED),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByBookerId() {
        user.setId(5L);
        Booking booking = Booking.builder().status(Status.WAITING).booker(user)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        repository.save(booking);

        List<Booking> bookings = repository.findByBookerId(5L, PageRequest.of(0, 2))
                .stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByBookerIdAndStatusIs() {
        user.setId(3L);
        Booking booking = Booking.builder().status(Status.WAITING).booker(user)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).booker(user)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByBookerIdAndStatusIs(3L, Status.REJECTED, PageRequest.of(0, 2))
                .stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfter() {
        user.setId(9L);
        Booking booking = Booking.builder().status(Status.WAITING).booker(user)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).booker(user)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now().minusDays(1)).build();

        userRepository.save(user);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByBookerIdAndStartBeforeAndEndAfter(9L, LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByBookerIdAndEndBefore() {
        user.setId(11L);
        Booking booking = Booking.builder().status(Status.WAITING).booker(user)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).booker(user)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();

        userRepository.save(user);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByBookerIdAndEndBefore(11L, LocalDateTime.now(),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
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
        user.setId(4L);
        item.setId(3L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdAndStatus(4L, Status.REJECTED,
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByOwnerId() {
        user.setId(7L);
        item.setOwner(user);
        item.setId(7L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerId(7L, PageRequest.of(0, 2))
                .stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(2, bookings.size(), "Не возвращает список с 2");
        assertEquals(booking, bookings.get(0), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(1), "Не возвращает список с 2");
    }

    @Test
    void findByOwnerIdCurrent() {
        user.setId(8L);
        item.setId(8L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now()).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now().minusDays(1)).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdCurrent(8L, LocalDateTime.now(),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }

    @Test
    void findByOwnerIdPast() {
        user.setId(6L);
        item.setOwner(user);
        item.setId(5L);
        Booking booking = Booking.builder().status(Status.WAITING).item(item)
                .end(LocalDateTime.now().plusDays(1)).start(LocalDateTime.now()).build();
        Booking booking1 = Booking.builder().status(Status.REJECTED).item(item)
                .end(LocalDateTime.now().minusDays(1)).start(LocalDateTime.now()).build();

        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
        repository.save(booking1);

        List<Booking> bookings = repository.findByOwnerIdPast(6L, LocalDateTime.now(),
                PageRequest.of(0, 2)).stream().collect(toList());

        assertNotNull(bookings, "Не возвращает список");
        assertEquals(1, bookings.size(), "Не возвращает список с 1");
        assertEquals(booking1, bookings.get(0), "Не возвращает список с 1");
    }*/
}