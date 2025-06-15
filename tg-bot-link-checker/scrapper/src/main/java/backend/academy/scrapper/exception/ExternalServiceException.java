package backend.academy.scrapper.exception;

import backend.academy.scrapper.exception.annotation.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
@ErrorCode(code = "502_BAD_GATEWAY", description = "Ошибка внешнего сервиса")
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}
