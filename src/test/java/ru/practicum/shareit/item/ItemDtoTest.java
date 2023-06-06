package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDtoTest {
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .owner(User.builder().id(1L).name("Nik").email("user@mail").build())
            .name("name")
            .description("description")
            .available(true)
            .build();

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateFailDescriptionItem() {
        itemDto.setDescription("");

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertEquals(1, violations.size(), "Создаётся пустой description");

        itemDto.setDescription(null);

        violations = validator.validate(itemDto);
        assertEquals(1, violations.size(), "Создаётся null description");
    }

    @Test
    void validateFailNameItem() {
        itemDto.setName("");

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertEquals(1, violations.size(), "Создаётся пустой name");

        itemDto.setName(null);

        violations = validator.validate(itemDto);
        assertEquals(1, violations.size(), "Создаётся null name");
    }

    @Test
    void validateFailAvailableItem() {
        itemDto.setAvailable(null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertEquals(1, violations.size(), "Создаётся null available");
    }

}