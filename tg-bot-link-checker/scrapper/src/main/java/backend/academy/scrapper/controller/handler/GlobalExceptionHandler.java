package backend.academy.scrapper.controller.handler;

import backend.academy.scrapper.AppConfig;
import backend.academy.scrapper.exception.annotation.ErrorCode;
import backend.academy.scrapper.exception.response.ApiErrorResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AppConfig appConfig;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        ErrorCode errorCodeAnnotation = ex.getClass().getAnnotation(ErrorCode.class);

        if (errorCodeAnnotation != null) {
            String errorCode = errorCodeAnnotation.code();
            String description = errorCodeAnnotation.description();

            log.error("Ошибка", ex);
            return buildErrorResponse(errorCode, description, ex);
        }

        log.error("Неизвестная ошибка", ex);
        return buildErrorResponse("500_INTERNAL_SERVER_ERROR", "Внутренняя ошибка сервера", ex);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        String headerName = ex.getHeaderName();
        log.warn("Отсутствует обязательный заголовок: {}", headerName, ex);
        return buildErrorResponse("400_BAD_REQUEST", "Отсутствует обязательный заголовок: " + headerName, ex);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(String errorCode, String description, Exception ex) {
        return ResponseEntity.status(
                        HttpStatus.valueOf(Integer.parseInt(errorCode.split("_")[0])))
                .body(new ApiErrorResponse(
                        description, errorCode, ex.getClass().getSimpleName(), ex.getMessage(), getStackTrace(ex)));
    }

    private List<String> getStackTrace(Exception ex) {
        if (!appConfig.includeStacktrace()) return List.of();
        return Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }
}
