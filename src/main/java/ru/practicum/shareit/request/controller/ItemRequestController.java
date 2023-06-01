package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return service.add(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return service.getById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return service.getAllByUser(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return service.getAll(userId, from, size);
    }
}
