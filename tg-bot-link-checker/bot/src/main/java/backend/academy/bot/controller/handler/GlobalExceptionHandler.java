package backend.academy.bot.controller.handler;

import backend.academy.bot.AppConfig;
import backend.academy.bot.exception.response.ApiErrorResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final AppConfig appConfig;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("Ошибка API: {}", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApiErrorResponse(
                        ex.getReason(),
                        ex.getStatusCode().toString(),
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
                        getStackTrace(ex)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        log.error("Неизвестная ошибка:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        "Внутренняя ошибка сервера",
                        HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
                        getStackTrace(ex)));
    }

    private List<String> getStackTrace(Exception ex) {
        if (!appConfig.includeStacktrace()) return List.of();
        return Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }
}
