package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateRequestDescriptionIsBlank() {
        RequestDto requestDto = new RequestDto("");

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size(), "Создаётся пустой description");
    }

    @Test
    void validateRequestDescriptionIsNull() {
        RequestDto requestDto = new RequestDto(null);

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(requestDto);
        assertEquals(1, violations.size(), "Создаётся пустой description");
    }
}