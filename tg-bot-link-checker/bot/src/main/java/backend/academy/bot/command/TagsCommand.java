package backend.academy.bot.command;

import backend.academy.bot.constants.BotMessages;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import backend.academy.bot.state.UserState;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagsCommand implements Command {
    private final UserSessionRepository sessionRepository;

    private final String SKIP_MESSAGE = "пропустить";

    @Override
    public String execute(long chatId, String message, UserSession session) {
        if (!message.equalsIgnoreCase(SKIP_MESSAGE)) {
            session.pendingTags(Arrays.asList(message.split("\\s+")));
        }
        session.state(UserState.AWAITING_FILTERS);
        sessionRepository.save(session);
        return BotMessages.ENTER_FILTERS;
    }
}
