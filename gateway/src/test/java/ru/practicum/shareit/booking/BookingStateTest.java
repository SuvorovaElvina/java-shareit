package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void fromAll() {
        Optional<BookingState> state = BookingState.from("all");

        assertTrue(state.isPresent());
    }

    @Test
    void fromCurrent() {
        Optional<BookingState> state = BookingState.from("cuRRent");

        assertTrue(state.isPresent());
    }

    @Test
    void fromFuture() {
        Optional<BookingState> state = BookingState.from("fuTure");

        assertTrue(state.isPresent());
    }

    @Test
    void fromPast() {
        Optional<BookingState> state = BookingState.from("pasT");

        assertTrue(state.isPresent());
    }

    @Test
    void fromRejected() {
        Optional<BookingState> state = BookingState.from("ReJeCTeD");

        assertTrue(state.isPresent());
    }

    @Test
    void fromWaiting() {
        Optional<BookingState> state = BookingState.from("WAITING");

        assertTrue(state.isPresent());
    }

    @Test
    void fromUnknown() {
        Optional<BookingState> state = BookingState.from("qwq");

        assertTrue(state.isEmpty());
    }

}