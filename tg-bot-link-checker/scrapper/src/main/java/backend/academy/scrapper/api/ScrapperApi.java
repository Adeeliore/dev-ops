package backend.academy.scrapper.api;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.exception.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Scrapper API", description = "API для управления отслеживаемыми ссылками и чатами Telegram")
@RequestMapping("/api/v1")
public interface ScrapperApi {

    @Operation(
            summary = "Зарегистрировать Telegram-чат",
            description = "Создает новую запись в системе для хранения ссылок, привязанную к Telegram-чату.",
            parameters = @Parameter(name = "id", description = "ID Telegram-чата", required = true),
            responses = {
                @ApiResponse(responseCode = "200", description = "Чат успешно зарегистрирован"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @PostMapping("/tg-chat/{id}")
    void registerChat(@PathVariable long id);

    @Operation(
            summary = "Удалить Telegram-чат",
            description = "Удаляет Telegram-чат и все связанные с ним отслеживаемые ссылки.",
            parameters = @Parameter(name = "id", description = "ID Telegram-чата", required = true),
            responses = {
                @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Чат не существует",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @DeleteMapping("/tg-chat/{id}")
    void deleteChat(@PathVariable long id);

    @Operation(
            summary = "Получить все отслеживаемые ссылки",
            description = "Возвращает список всех ссылок, отслеживаемых в рамках указанного Telegram-чата.",
            parameters = @Parameter(name = "Tg-Chat-Id", description = "ID Telegram-чата", required = true),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ссылки успешно получены",
                        content = @Content(schema = @Schema(implementation = ListLinksResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @GetMapping("/links")
    ListLinksResponse getTrackedLinks(@RequestHeader("Tg-Chat-Id") long chatId);

    @Operation(
            summary = "Добавить отслеживаемую ссылку",
            description = "Добавляет новую ссылку в список отслеживаемых ссылок для указанного Telegram-чата.",
            parameters = @Parameter(name = "Tg-Chat-Id", description = "ID Telegram-чата", required = true),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ссылка успешно добавлена",
                        content = @Content(schema = @Schema(implementation = LinkResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @PostMapping("/links")
    LinkResponse addLink(@RequestHeader("Tg-Chat-Id") long chatId, @RequestBody AddLinkRequest request);

    @Operation(
            summary = "Удалить отслеживаемую ссылку",
            description = "Прекращает отслеживание указанной ссылки для Telegram-чата.",
            parameters = @Parameter(name = "Tg-Chat-Id", description = "ID Telegram-чата", required = true),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ссылка успешно убрана",
                        content = @Content(schema = @Schema(implementation = LinkResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Ссылка не найдена",
                        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @DeleteMapping("/links")
    LinkResponse removeLink(@RequestHeader("Tg-Chat-Id") long chatId, @RequestBody RemoveLinkRequest request);
}
