package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private final UserMapper mapper;

    @Override
    public UserDto add(UserDto user) {
        return mapper.toUserDto(repository.save(mapper.toUser(user)));
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = getUser(id);
        updateName(user, userDto);
        updateEmail(user, userDto);
        repository.save(user);
        return mapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long id) {
        return mapper.toUserDto(getUser(id));
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public void delete(long id) {
        getUser(id);
        repository.deleteById(id);
    }

    @Override
    public User getUser(long id) {
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

    private void updateName(User user, UserDto userDto) {
        if (Optional.ofNullable(userDto.getName()).isPresent()) {
            if (!userDto.getName().isBlank()) {
                user.setName(userDto.getName());
            }
        }
    }

    private void updateEmail(User user, UserDto userDto) {
        if (Optional.ofNullable(userDto.getEmail()).isPresent()){
            if (!userDto.getEmail().isBlank()) {
                user.setEmail(userDto.getEmail());
            }
        }
    }
}
