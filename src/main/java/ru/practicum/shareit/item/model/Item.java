package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    long id;
    User owner;
    @NotBlank(message = "Имя не должно быть пустым.")
    String name;
    @NotBlank(message = "Описание не должно быть пустым.")
    String description;
    boolean available;
    ItemRequest request;
}
