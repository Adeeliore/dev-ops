package backend.academy.bot.exception;

import backend.academy.bot.exception.response.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ScrapperApiException extends RuntimeException {
    private final ApiErrorResponse errorResponse;

    public ScrapperApiException(ApiErrorResponse errorResponse) {
        super(errorResponse.description());
        this.errorResponse = errorResponse;
    }
}
