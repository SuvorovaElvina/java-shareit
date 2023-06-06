package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.reposiroty.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void findByOwnerId() {
        User user = User.builder().id(1L).name("name").email("user@mail").build();
        User user1 = User.builder().id(2L).name("name").email("users@mail").build();
        Item item = Item.builder().id(4L).owner(user).name("name").description("desc").build();
        Item item1 = Item.builder().id(4L).owner(user).name("name").description("desc").build();
        Item item2 = Item.builder().id(4L).owner(user1).name("name").description("desc").build();

        userRepository.save(user);
        userRepository.save(user1);
        repository.save(item);
        repository.save(item1);
        repository.save(item2);

        List<Item> items = repository.findByOwnerId(1L, PageRequest.of(0, 3))
                .stream().collect(toList());

        assertEquals(2, items.size(), "не возвращает список из 2");
    }

    @Test
    void findByRequestIdOrderByIdAsc() {
        ItemRequest request = ItemRequest.builder().id(1L).description("desc").build();
        ItemRequest request1 = ItemRequest.builder().id(2L).description("desc").build();
        Item item = Item.builder().id(4L).name("name").description("desc").request(request).build();
        Item item1 = Item.builder().id(4L).name("name").description("desc").request(request1).build();
        Item item2 = Item.builder().id(4L).name("name").description("desc").request(request).build();

        requestRepository.save(request);
        requestRepository.save(request1);
        repository.save(item);
        repository.save(item1);
        repository.save(item2);

        List<Item> items = repository.findByRequestIdOrderByIdAsc(1L);

        assertEquals(2, items.size(), "не возвращает список из 2");
    }

    @Test
    void search() {
        Item item = Item.builder().id(4L).name("name").description("desc").available(true).build();
        Item item1 = Item.builder().id(2L).name("text").description("text").available(true).build();

        repository.save(item);
        repository.save(item1);

        List<Item> items = repository.search("text", PageRequest.of(0, 3))
                .stream().collect(toList());

        assertEquals(1, items.size(), "возвращает не 1 нужный запрос");
        assertEquals(item1, items.get(0), "возвращает не нужный запрос");
    }
}