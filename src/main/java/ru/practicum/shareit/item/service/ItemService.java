package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto add(long id, ItemDto item);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getById(long id, long userId);

    Item getItem(long id);

    List<ItemDto> getAll(long userId);

    List<ItemDto> searchText(long userId, String str);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
