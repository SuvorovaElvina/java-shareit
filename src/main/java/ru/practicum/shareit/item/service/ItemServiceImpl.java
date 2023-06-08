package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService service;
    private final ItemRequestService requestService;
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto add(long id, ItemDto itemDto) {
        Item item = mapper.toItem(itemDto);
        item.setOwner(service.getUser(id));
        if (itemDto.getRequestId() != null) {
            item.setRequest(requestService.reply(itemDto.getRequestId()));
        }
        return mapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        User user = service.getUser(userId);
        Item item = getItem(itemId);
        if (item.getOwner().getId().equals(user.getId())) {
            updateName(item, itemDto);
            updateDescription(item, itemDto);
            updateAvailable(item, itemDto);
            repository.save(item);
            return mapper.toItemDto(item);
        } else {
            throw new NotFoundException(String.format("Вы не являетесь владельцем вещи под номером %d", itemId));
        }
    }

    @Override
    public ItemDto getById(long id, long userId) {
        Item item = getItem(id);
        ItemDto itemDto = mapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(id, LocalDateTime.now())
                    .ifPresent(booking -> itemDto.setLastBooking(bookingMapper.toItemsBookingDto(booking)));
            bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(id, LocalDateTime.now(), Status.REJECTED)
                    .ifPresent(booking -> itemDto.setNextBooking(bookingMapper.toItemsBookingDto(booking)));
        }
        itemDto.setComments(commentRepository.findAllByItemId(item.getId()).orElse(List.of())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(toList()));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(long userId, int from, int size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Page<Item> items = repository.findByOwnerId(userId, PageRequest.of(pageNumber, size, Sort.by("id").ascending()));
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = mapper.toItemDto(item);

            bookingRepository.findFirst1ByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())
                    .ifPresent(booking -> itemDto.setLastBooking(bookingMapper.toItemsBookingDto(booking)));
            bookingRepository.findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(item.getId(), LocalDateTime.now(), Status.REJECTED)
                    .ifPresent(booking -> itemDto.setNextBooking(bookingMapper.toItemsBookingDto(booking)));

            itemDto.setComments(commentRepository.findAllByItemId(item.getId()).orElse(List.of())
                    .stream()
                    .map(commentMapper::toCommentDto)
                    .collect(toList()));

            itemsDto.add(itemDto);
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchText(long userId, String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        } else {
            int pageNumber = (int) Math.ceil((double) from / size);
            Page<Item> items = repository.search(text, PageRequest.of(pageNumber, size));
            return items.stream()
                    .map(mapper::toItemDto)
                    .collect(toList());
        }
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Optional<List<Booking>> bookings =
                bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(itemId, userId, LocalDateTime.now(), Status.REJECTED);
        if (bookings.isPresent() && !bookings.get().isEmpty()) {
            Comment comment = commentMapper.toComment(service
                    .getUser(userId), getItem(itemId), commentDto, LocalDateTime.now());
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException("Вы не бронировали эту вещь или срок бронирвания ещё не истёк.");
        }
    }

    @Override
    public Item getItem(long id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        }
        Optional<Item> optional = repository.findById(id);
        return optional.orElseThrow(() -> new NotFoundException(String.format("Вещь с id %d - не существует.", id)));
    }

    private void updateName(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            if (!itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
        }
    }

    private void updateDescription(Item item, ItemDto itemDto) {
        if (itemDto.getDescription() != null) {
            if (!itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
        }
    }

    private void updateAvailable(Item item, ItemDto itemDto) {
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }
}
