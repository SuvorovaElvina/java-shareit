package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User add(UserDto user);

    User update(long id, UserDto user);

    User getById(long id);

    List<User> getAll();

    void delete(long id);
}
