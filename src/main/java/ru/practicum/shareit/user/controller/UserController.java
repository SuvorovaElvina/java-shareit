package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @PostMapping
    @Validated({Create.class})
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @PatchMapping("/{userId}")
    @Validated({Update.class})
    public UserDto updateUser(@PathVariable("userId") long id, @Valid @RequestBody UserDto userDto) {
        return service.update(id, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return service.getAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long id) {
        service.delete(id);
    }
}
