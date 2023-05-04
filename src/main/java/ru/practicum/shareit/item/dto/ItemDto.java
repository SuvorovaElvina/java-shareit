package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    User owner;
    @NotBlank(message = "Имя не должно быть пустым.")
    String name;
    @NotBlank(message = "Описание не должно быть пустым.")
    String description;
    @NotNull
    Boolean available;
    Long request;
}
