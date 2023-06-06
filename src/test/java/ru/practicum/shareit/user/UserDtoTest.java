package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("user@mail.ru")
            .build();
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateFailNameUser() {
        userDto.setName("");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size(), "Создаётся пустой email");

        userDto.setName(null);

        violations = validator.validate(userDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateFailEmailUser() {
        userDto.setEmail("user.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size(), "Создаётся не email");

        userDto.setEmail("");

        violations = validator.validate(userDto);
        assertEquals(1, violations.size(), "Создаётся пустой email");

        userDto.setEmail(null);

        violations = validator.validate(userDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }
}