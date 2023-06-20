package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.validation.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateItemNameIsBlank() {
        ItemDto itemDto = new ItemDto(1L, "", "desc", true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся пустой name");
    }

    @Test
    void validateItemDescriptionIsBlank() {
        ItemDto itemDto = new ItemDto(1L, "name", "", true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся пустой description");
    }

    @Test
    void validateItemNameIsNull() {
        ItemDto itemDto = new ItemDto(1L, null, "desc", true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся name null");
    }

    @Test
    void validateItemDescriptionIsNull() {
        ItemDto itemDto = new ItemDto(1L, "name", null, true, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся description null");
    }

    @Test
    void validateItemAvailableIsNull() {
        ItemDto itemDto = new ItemDto(1L, "name", "desc", null, 1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(1, violations.size(), "Создаётся available null");
    }
}