package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody BookingDto bookingDto) {
        return service.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId, @RequestParam(required = false) Boolean approved) {
        return service.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return service.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return service.getAllByUser(userId, state, from, size);

    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        return service.getAllByOwner(ownerId, state, from, size);
    }
}
