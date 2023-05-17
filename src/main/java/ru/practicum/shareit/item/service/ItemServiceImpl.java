package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService service;
    private final ItemRepository repository;

    @Override
    public Item add(long id, Item item) {
        item.setOwner(service.getById(id));
        return repository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
        User user = service.getById(userId);
        Item item = getById(itemId);
        if (item.getOwner().getId().equals(user.getId())) {
            updateName(item, itemDto);
            updateDescription(item, itemDto);
            updateAvailable(item, itemDto);
            repository.save(item);
        } else {
            throw new NotFoundException(String.format("Вы не являетесь владельцем вещи под номером %d", itemId));
        }
        return item;
    }

    @Override
    public Item getById(long id) {
        validateId(id);
        return repository.findById(id).get();
    }

    @Override
    public List<Item> getAll(long userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : repository.findAll()) {
            if (item.getOwner().getId().equals(userId)) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<Item> searchText(long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return repository.search(text);
        }
    }

    private void validateId(long id) {
        try {
            repository.findById(id).get();
        } catch (NoSuchElementException e) {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException(String.format("Пользователь с id %d - не существует.", id));
            }
        }
    }

    private void updateName(Item item, ItemDto itemDto) {
        try {
            if (!itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    private void updateDescription(Item item, ItemDto itemDto) {
        try {
            if (!itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    private void updateAvailable(Item item, ItemDto itemDto) {
        try {
            itemDto.getAvailable().toString();
            item.setAvailable(itemDto.getAvailable());
        } catch (NullPointerException e) {
            return;
        }
    }
}
