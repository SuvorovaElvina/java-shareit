package ru.practicum.shareit.request;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemRequestRepositoryTest {
    /*@Autowired
    private ItemRequestRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByOwnerId() {
        User user = User.builder().id(16L).name("name").email("user@mail").build();
        ItemRequest request = ItemRequest.builder().id(4L).owner(user).description("desc").build();

        userRepository.save(user);
        repository.save(request);

        List<ItemRequest> requests = repository.findByOwnerId(16L, PageRequest.of(0, 1)).stream().collect(toList());

        assertEquals(1, requests.size(), "Не даёт нужный результат");
    }

    @Test
    void findByOwnerIdNotWithOwnerId_ListEmpty() {
        User user = User.builder().id(14L).name("name").email("user@mail").build();
        ItemRequest request = ItemRequest.builder().id(4L).owner(user).description("desc").build();

        userRepository.save(user);
        repository.save(request);

        List<ItemRequest> requests = repository.findByOwnerIdNot(14L, PageRequest.of(0, 1)).stream().collect(toList());

        assertEquals(0, requests.size(), "Выводит не то что нужно");
    }

    @Test
    void findByOwnerIdNot() {
        User user = User.builder().id(15L).name("name").email("user@mail").build();
        ItemRequest request = ItemRequest.builder().id(4L).owner(user).description("desc").build();

        userRepository.save(user);
        repository.save(request);

        List<ItemRequest> requests = repository.findByOwnerIdNot(16L, PageRequest.of(0, 1)).stream().collect(toList());

        assertEquals(1, requests.size(), "Выводит не то что нужно");
    }*/
}