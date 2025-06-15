package backend.academy.scrapper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Обновление отслеживаемой ссылки")
public record LinkUpdate(
        @Schema(description = "ID обновления", example = "123") Long id,
        @Schema(description = "URL отслеживаемой ссылки", example = "https://github.com/example/repo") String url,
        @Schema(description = "Описание обновления", example = "Новый комментарий в репозитории") String description,
        @Schema(description = "ID Telegram-чатов, которым отправить обновление") List<Long> tgChatIds) {}
