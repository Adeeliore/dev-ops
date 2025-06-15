package backend.academy.bot.command;

import backend.academy.bot.constants.BotMessages;
import backend.academy.bot.model.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnknownCommand implements Command {

    @Override
    public String execute(long chatId, String message, UserSession session) {
        return BotMessages.UNKNOWN_COMMAND;
    }
}
