package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class ErrorHandlerControllerTest {
    @MockBean
    private UserService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void getNotFoundException() throws Exception {
        when(service.getById(anyLong())).thenThrow(new NotFoundException(""));

        mvc.perform(get("/users/1")).andExpect(status().isNotFound());
    }

    @Test
    void getIncorrectCountException() throws Exception {
        when(service.getById(anyLong())).thenThrow(new IncorrectCountException(""));

        mvc.perform(get("/users/1")).andExpect(status().isNotFound());
    }

    @Test
    void getRuntimeException() throws Exception {
        when(service.getById(anyLong())).thenThrow(new RuntimeException(""));

        mvc.perform(get("/users/1")).andExpect(status().isInternalServerError());
    }

    @Test
    void getUnknownStateException() throws Exception {
        when(service.getById(anyLong())).thenThrow(new UnknownStateException("Не найдено"));

        mvc.perform(get("/users/1")).andExpect(status().isBadRequest());
    }

    @Test
    void getValidationException() throws Exception {
        when(service.getById(anyLong())).thenThrow(new ValidationException("Не найдено"));

        mvc.perform(get("/users/1")).andExpect(status().isBadRequest());
    }
}