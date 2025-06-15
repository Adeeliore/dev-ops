package backend.academy.scrapper.exception;

import backend.academy.scrapper.exception.annotation.ErrorCode;
import backend.academy.scrapper.exception.response.ApiErrorResponse;
import lombok.Getter;

@Getter
@ErrorCode(code = "500_INTERNAL_SERVER_ERROR", description = "Ошибка Scrapper API")
public class ScrapperApiException extends RuntimeException {
    private final ApiErrorResponse errorResponse;

    public ScrapperApiException(ApiErrorResponse errorResponse) {
        super(errorResponse.description());
        this.errorResponse = errorResponse;
    }
}
