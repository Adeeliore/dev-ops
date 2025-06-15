package backend.academy.bot.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Описание ошибки API")
public record ApiErrorResponse(
        @Schema(description = "Описание ошибки", example = "Некорректные параметры запроса") String description,
        @Schema(description = "Код ошибки", example = "400_BAD_REQUEST") String code,
        @Schema(description = "Имя исключения", example = "IllegalArgumentException") String exceptionName,
        @Schema(description = "Сообщение исключения", example = "Invalid request data") String exceptionMessage,
        @Schema(description = "Стектрейс ошибки") List<String> stacktrace) {}
