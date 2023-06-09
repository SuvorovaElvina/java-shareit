package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        return service.add(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return service.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return service.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        return service.searchText(userId, text, from, size);
    }
}
