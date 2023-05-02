package ru.practicum.shareit.user.repositiry;

import ru.practicum.shareit.user.User;

import java.util.Map;

public interface UserRepository {
    User add(User user);

    User update(User user);

    void delete(long id);

    Map<Long, User> getAll();
}
