package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User add(User user) {
        for (User user1 : repository.getAll().values()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new DuplicateException("Эта почта уже используется, введите другую.");
            }
        }
        return repository.add(user);
    }

    @Override
    public User update(long id, UserDto userDto) {
        User user = getById(id);
        updateName(user, userDto);
        updateEmail(user, userDto);
        return repository.update(user);
    }

    @Override
    public User getById(long id) {
        validateId(id);
        return repository.getAll().get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(repository.getAll().values());
    }

    @Override
    public void delete(long id) {
        validateId(id);
        repository.delete(id);
    }

    private void validateId(long id) {
        if (id < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        } else if (!repository.getAll().containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id %d - не существует.", id));
        }
    }

    private void updateName(User user, UserDto userDto) {
        try {
            if (!userDto.getName().isBlank()) {
                user.setName(userDto.getName());
            }
        } catch (NullPointerException e) {
        }
    }

    private void updateEmail(User user, UserDto userDto) {
        try {
            if (!userDto.getEmail().isBlank()) {
                for (User user1 : repository.getAll().values()) {
                    if (user1.getEmail().equals(userDto.getEmail()) & (!user.getId().equals(user1.getId()))) {
                        throw new DuplicateException("Эта почта уже используется, введите другую.");
                    }
                }
                user.setEmail(userDto.getEmail());
            }
        } catch (NullPointerException e) {
        }
    }
}
