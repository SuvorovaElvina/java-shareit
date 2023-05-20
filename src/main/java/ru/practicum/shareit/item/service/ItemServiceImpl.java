package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService service;
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto add(long id, ItemDto itemDto) {
        Item item = mapper.toItem(itemDto);
        item.setOwner(service.getById(id));
        return mapper.toSimpleItemDto(repository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        User user = service.getById(userId);
        Item item = getItem(itemId);
        if (item.getOwner().getId().equals(user.getId())) {
            updateName(item, itemDto);
            updateDescription(item, itemDto);
            updateAvailable(item, itemDto);
            repository.save(item);
            return mapper.toSimpleItemDto(item);
        } else {
            throw new NotFoundException(String.format("Вы не являетесь владельцем вещи под номером %d", itemId));
        }
    }

    @Override
    public ItemDto getById(long id, long userId) {
        Item item = getItem(id);
        try {
            if (item.getOwner().getId().equals(userId)) {
                return mapper.toBookingItemDto(item,
                        bookingMapper.toItemsBookingDto(bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(id, LocalDateTime.now())),
                        bookingMapper.toItemsBookingDto(bookingRepository.findFirst1ByItemIdAndStartAfterOrderByStartAsc(id, LocalDateTime.now())));
            } else {
                return mapper.toSimpleItemDto(item);
            }
        } catch (NullPointerException e) {
            return mapper.toSimpleItemDto(item);
        }
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        List<Item> items = repository.findByOwnerIdOrderByIdAsc(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item: items) {
            try {
                itemsDto.add(mapper.toBookingItemDto(item,
                        bookingMapper.toItemsBookingDto(bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())),
                        bookingMapper.toItemsBookingDto(bookingRepository.findFirst1ByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()))));
            } catch (NullPointerException e) {
                itemsDto.add(mapper.toSimpleItemDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchText(long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return repository.search(text).stream()
                    .map(mapper::toSimpleItemDto)
                    .collect(toList());
        }
    }

    @Override
    public Item getItem(long id) {
        Optional<Item> optional = repository.findById(id);
        if (optional.isEmpty()) {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException(String.format("Вещь с id %d - не существует.", id));
            }
        } else {
            return optional.get();
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
