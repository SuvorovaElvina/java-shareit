package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repositiry.UserRepository;
import ru.practicum.shareit.user.repositiry.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository = new UserRepositoryImpl();
    private final UserMapper mapper = new UserMapper();

    @Override
    public User add(UserDto userDto) {
        User user = mapper.toUser(userDto);
        return repository.add(user);
    }

    @Override
    public User update(long id, UserDto userDto) {
        User user = getById(id);
        name(user, userDto);
        email(user, userDto);
        return repository.update(user);
    }

    private void name(User user, UserDto userDto) {
        try {
            if (!userDto.getName().isEmpty()) {
                user.setName(userDto.getName());
            }
        } catch (NullPointerException e) {
        }
    }

    private void email(User user, UserDto userDto) {
        try {
            if (!userDto.getEmail().isBlank()) {
                for (User user1 : repository.getAll().values()) {
                    if (user1.getEmail().equals(userDto.getEmail()) & (!Objects.equals(user.getId(), user1.getId()))) {
                        throw new DuplicateException("Эта почта уже используется, введите другую.");
                    }
                }
                user.setEmail(userDto.getEmail());
            }
        } catch (NullPointerException e) {
        }
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
        }
        try {
            repository.getAll().get(id);
        } catch (NullPointerException e) {
            throw new NotFoundException(String.format("Пользователь с id %d - не существует.", id));
        }
    }
}
