package backend.academy.bot.api;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.exception.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Bot API", description = "API для взаимодействия с Telegram-ботом")
@RequestMapping("/updates")
public interface BotApi {

    @PostMapping
    @Operation(
            summary = "Отправить обновление",
            description = "Принимает обновления по отслеживаемым ссылкам и передаёт их в Telegram-бот.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Обновление обработано"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    ResponseEntity<Void> receiveUpdate(@RequestBody LinkUpdate update);
}
