package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item add(Item item);

    Item update(Item item);

    void delete(long id);

    Optional<Item> getById(long id);

    List<Item> getAll();
}
