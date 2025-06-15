package backend.academy.bot.service.impl;

import backend.academy.bot.command.BackCommand;
import backend.academy.bot.command.FiltersCommand;
import backend.academy.bot.command.LinkCommand;
import backend.academy.bot.command.TagsCommand;
import backend.academy.bot.command.UnknownCommand;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import backend.academy.bot.service.BotStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotStateServiceImpl implements BotStateService {
    private final UserSessionRepository sessionRepository;
    private final LinkCommand linkCommand;
    private final TagsCommand tagsCommand;
    private final FiltersCommand filtersCommand;
    private final BackCommand backCommand;
    private final UnknownCommand unknownCommand;

    @Override
    public String processMessage(long chatId, String message) {
        UserSession session = sessionRepository.getOrCreateSession(chatId);

        if (message.equalsIgnoreCase("назад")) {
            return backCommand.execute(chatId, message, session);
        }

        return switch (session.state()) {
            case AWAITING_LINK -> linkCommand.execute(chatId, message, session);
            case AWAITING_TAGS -> tagsCommand.execute(chatId, message, session);
            case AWAITING_FILTERS -> filtersCommand.execute(chatId, message, session);
            default -> unknownCommand.execute(chatId, message, session);
        };
    }
}
