package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookItemRequestDtoTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateBookerItemIdIsNegative() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(-1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateBookerItemIdIsZero() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(0L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateBookerItemIdIsNull() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateBookerStartIsPast() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateBookerStartIsNull() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, null, LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateBookerEndIsPresent() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now());

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateBookerEndIsPast() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusMonths(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    void validateBookerEndIsNull() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);
        assertEquals(1, violations.size(), "Создаётся null email");
    }
}