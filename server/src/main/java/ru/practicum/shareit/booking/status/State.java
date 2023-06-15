package ru.practicum.shareit.booking.status;

public enum State {
    ALL, FUTURE, REJECTED, WAITING, CURRENT, PAST;

    public static State fromString(String state) {
        return State.valueOf(state);
    }
}
