package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        return mapper.toUserDto(service.add(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateEmailUser(@PathVariable("userId") long id, @RequestBody UserDto userDto) {
        return mapper.toUserDto(service.update(id, userDto));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long id) {
        return mapper.toUserDto(service.getById(id));
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return service.getAll()
                .stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long id) {
        service.delete(id);
    }
}
