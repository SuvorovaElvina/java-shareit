package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of(
                "error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }

    /*@ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ConstraintViolationException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of(
                "error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }*/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of(
                "error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnknownStateException(final UnknownStateException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleIncorrectCountException(final IncorrectCountException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of(
                "error", "Ошибка с параметром count.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of(
                "error", "Ошибка - не найдено.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleError(final RuntimeException e) {
        log.debug("Получен статус 500 Internal server error {}", e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }
}
