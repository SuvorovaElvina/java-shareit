package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;

    @NotBlank(message = "Имя не должно быть пустым.")
    @Size(max = 255)
    String name;

    @NotBlank(message = "Email не должно быть пустым.")
    @Email(message = "Введён не email.")
    @Size(max = 512)
    String email;
}
