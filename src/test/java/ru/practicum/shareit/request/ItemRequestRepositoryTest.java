package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.reposiroty.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
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
    }
}