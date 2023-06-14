package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

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
        itemDto.setComments(commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(toList()));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(long userId, int from, int size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Page<Item> itemsPage = repository.findByOwnerId(userId, PageRequest.of(pageNumber, size, Sort.by("id").ascending()));
        List<Item> items = itemsPage.toList();

        Map<Long, Booking> bookingsBeforeMap = bookingRepository.findByItemInAndStartBeforeOrderByStartDesc(items, LocalDateTime.now())
                .stream()
                .filter(booking -> booking.getItem() != null)
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity(), (b1, b2) -> b1));
        Map<Long, Booking> bookingsAfterMap = bookingRepository.findByItemInAndStartAfterAndStatusNotLikeOrderByStartAsc(items, LocalDateTime.now(), Status.REJECTED)
                .stream()
                .filter(booking -> booking.getItem() != null)
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity(), (b1, b2) -> b1));
        Map<Long, List<CommentDto>> commentsMap = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .filter(comment -> comment.getItem() != null)
                .collect(groupingBy(comment -> comment.getItem().getId(), Collectors.mapping(commentMapper::toCommentDto, Collectors.toList())));

        List<ItemDto> itemDtos = items
                .stream()
                .map(mapper::toItemDto)
                .peek(item -> {
                    item.setComments(commentsMap.getOrDefault(item.getId(), List.of()));
                    Optional.ofNullable(bookingsBeforeMap.get(item.getId()))
                            .ifPresent(booking -> item.setLastBooking(bookingMapper.toItemsBookingDto(booking)));
                    Optional.ofNullable(bookingsAfterMap.get(item.getId()))
                            .ifPresent(booking -> item.setNextBooking(bookingMapper.toItemsBookingDto(booking)));
                })
                .collect(toList());
        return itemDtos;
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
        List<Booking> bookings =
                bookingRepository.findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(itemId, userId, LocalDateTime.now(), Status.REJECTED);
        if (!bookings.isEmpty()) {
            Comment comment = commentMapper.toComment(service
                    .getUser(userId), getItem(itemId), commentDto, LocalDateTime.now());
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException("Вы не бронировали эту вещь или срок бронирвания ещё не истёк.");
        }
    }

    @Override
    public Item getItem(long id) {
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
