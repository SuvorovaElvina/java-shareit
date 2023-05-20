package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingDto booking);

    BookingDto update(long userId, long bookingId, String approved);

    BookingDto getById(long userId, long bookingId);

    List<BookingDto> getAllByUser(long userId, String state);

    List<BookingDto> getAllByOwner(long ownerId, String state);
}