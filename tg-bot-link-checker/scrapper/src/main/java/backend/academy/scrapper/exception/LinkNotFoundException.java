package backend.academy.scrapper.exception;

import backend.academy.scrapper.exception.annotation.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@ErrorCode(code = "404_NOT_FOUND", description = "Ссылка не найдена")
public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String link) {
        super("Ссылка не найдена: " + link);
    }
}
