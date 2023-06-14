package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    UserDto update(long id, UserDto user);

    UserDto getById(long id);

    List<UserDto> getAll();

    void delete(long id);

    User getUser(long id);
}
