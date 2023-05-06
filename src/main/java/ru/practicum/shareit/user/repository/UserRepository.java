package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User add(User user);

    User update(User user);

    void delete(long id);

    Optional<User> getById(long id);

    List<User> getAll();
}
