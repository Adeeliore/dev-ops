package backend.academy.bot.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.exception.response.ApiErrorResponse;
import backend.academy.bot.service.impl.UpdateService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"app.telegram-token=token"})
@ExtendWith(MockitoExtension.class)
class BotControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private UpdateService updateService;

    @Test
    @DisplayName("Получение обновления - успешный ответ 200")
    void shouldReceiveUpdate() {
        LinkUpdate update = new LinkUpdate(1L, "https://github.com/test/repo", "Обновление", List.of(123L));

        ResponseEntity<Void> response = restTemplate.postForEntity("/updates", update, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(updateService).processUpdate(update);
    }

    @Test
    @DisplayName("Получение некорректного обновления - ошибка 400")
    void shouldReturnBadRequestForInvalidUpdate() {
        ResponseEntity<ApiErrorResponse> response =
                restTemplate.postForEntity("/updates", null, ApiErrorResponse.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
