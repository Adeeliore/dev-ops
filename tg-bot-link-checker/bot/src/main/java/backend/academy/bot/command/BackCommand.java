package backend.academy.bot.command;

import backend.academy.bot.constants.BotMessages;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackCommand implements Command {
    private final UserSessionRepository sessionRepository;

    @Override
    public String execute(long chatId, String message, UserSession session) {
        session.rollback();
        sessionRepository.save(session);
        return switch (session.state()) {
            case AWAITING_LINK -> BotMessages.BACK_TO_LINK;
            case AWAITING_TAGS -> BotMessages.BACK_TO_TAGS;
            case AWAITING_FILTERS -> BotMessages.BACK_TO_FILTERS;
            default -> BotMessages.BACK_TO_MENU;
        };
    }
}
