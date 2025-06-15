package backend.academy.scrapper.exception;

import backend.academy.scrapper.exception.annotation.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@ErrorCode(code = "400_BAD_REQUEST", description = "Ссылка уже отслеживается")
public class DuplicateLinkException extends RuntimeException {
    private final String link;

    public DuplicateLinkException(String link) {
        super("Ссылка уже отслеживается: " + link);
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
