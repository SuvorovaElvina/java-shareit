package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemsBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public ItemsBookingDto toItemsBookingDto(Booking booking) {
        return ItemsBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .build();
    }
}
