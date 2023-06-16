package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateCreateUserNameIsBlank() {
        UserDto userDto = new UserDto(1L, "", "nik@mail.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        violations.forEach(ConstraintViolation::getMessage);
        assertEquals(1, violations.size(), "Создаётся name с пустой строкой");
    }

    @Test
    void validateCreateUserEmailIsBlank() {
        UserDto userDto = new UserDto(1L, "name", "");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся email с пустой строкой");
    }

    @Test
    void validateCreateUserNameIsNull() {
        UserDto userDto = new UserDto(1L, null, "nik@mail.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся name с null");
    }

    @Test
    void validateCreateUserEmailIsNull() {
        UserDto userDto = new UserDto(1L, "name", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся email с null");
    }

    @Test
    void validateUpdateUserNoEmail() {
        UserDto userDto = new UserDto(1L, "name", "@");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Update.class);
        assertEquals(1, violations.size(), "Создаётся не email");
    }

    @Test
    void validateCreateUserNoEmail() {
        UserDto userDto = new UserDto(1L, "name", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся не email");
    }
}