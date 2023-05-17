package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private long index = 1;

    @Override
    public User add(User user) {
        user.setId(index++);
        for (User user1 : repository.findAll()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new DuplicateException("Эта почта уже используется, введите другую.");
            }
        }
        return repository.save(user);
    }

    @Override
    public User update(long id, UserDto userDto) {
        User user = getById(id);
        updateName(user, userDto);
        updateEmail(user, userDto);
        return repository.save(user);
    }

    @Override
    public User getById(long id) {
        Optional<User> optional = repository.findById(id);
        if (optional.isEmpty()) {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException(String.format("Пользователь с id %d - не существует.", id));
            }
        } else {
            return optional.get();
        }
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public void delete(long id) {
        getById(id);
        repository.deleteById(id);
    }

    private void updateName(User user, UserDto userDto) {
        try {
            if (!userDto.getName().isBlank()) {
                user.setName(userDto.getName());
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    private void updateEmail(User user, UserDto userDto) {
        try {
            if (!userDto.getEmail().isBlank()) {
                for (User user1 : repository.findAll()) {
                    if (user1.getEmail().equals(userDto.getEmail()) && (!user.getId().equals(user1.getId()))) {
                        throw new DuplicateException("Эта почта уже используется, введите другую.");
                    }
                }
                user.setEmail(userDto.getEmail());
            }
        } catch (NullPointerException e) {
            return;
        }
    }
}
