package backend.academy.bot.command;

import backend.academy.bot.constants.BotMessages;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import backend.academy.bot.service.TrackingService;
import backend.academy.bot.service.impl.LinkValidatorService;
import backend.academy.bot.state.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkCommand implements Command {
    private final LinkValidatorService linkValidatorService;
    private final TrackingService trackingService;
    private final UserSessionRepository sessionRepository;

    @Override
    public String execute(long chatId, String message, UserSession session) {
        if (!linkValidatorService.isValid(message)) {
            return BotMessages.INVALID_LINK;
        }

        boolean isAlreadyTracked = trackingService
                .listTrackedLinks(chatId)
                .map(response ->
                        response.links().stream().anyMatch(link -> link.url().equals(message)))
                .blockOptional()
                .orElse(false);

        if (isAlreadyTracked) {
            return BotMessages.LINK_ALREADY_TRACKED;
        }

        session.pendingLink(message);
        session.state(UserState.AWAITING_TAGS);
        sessionRepository.save(session);
        return BotMessages.ENTER_TAGS;
    }
}
