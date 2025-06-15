package backend.academy.scrapper.exception;

import backend.academy.scrapper.exception.annotation.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@ErrorCode(code = "400_BAD_REQUEST", description = "Чат не зарегистрирован")
public class ChatNotRegisteredException extends RuntimeException {
    public ChatNotRegisteredException(long chatId) {
        super("Чат не зарегистрирован: " + chatId);
    }
}
