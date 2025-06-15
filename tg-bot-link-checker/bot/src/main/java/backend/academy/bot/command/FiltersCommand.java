package backend.academy.bot.command;

import backend.academy.bot.constants.BotMessages;
import backend.academy.bot.constants.Constants;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import backend.academy.bot.service.TrackingService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FiltersCommand implements Command {
    private final TrackingService trackingService;
    private final UserSessionRepository sessionRepository;

    private final String SKIP_MESSAGE = "пропустить";

    @Override
    public String execute(long chatId, String message, UserSession session) {
        if (!message.equalsIgnoreCase(SKIP_MESSAGE)) {
            List<String> filters = Arrays.asList(message.split("\\s+"));
            for (String filter : filters) {
                if (!isValidFilter(filter)) {
                    return String.format(BotMessages.INVALID_FILTER, filter);
                }
            }
            session.pendingFilters(filters);
        }

        String link = session.pendingLink();
        Set<String> tags = new HashSet<>(session.pendingTags());
        Set<String> filters = new HashSet<>(session.pendingFilters());

        return trackingService
                .trackLink(chatId, link, tags, filters)
                .map(response -> {
                    session.reset();
                    sessionRepository.save(session);
                    return String.format(
                            BotMessages.LINK_TRACKED_TEMPLATE, response.url(), response.tags(), response.filters());
                })
                .onErrorReturn(BotMessages.TRACKING_ERROR)
                .block();
    }

    private boolean isValidFilter(String filter) {
        return filter.equals(Constants.FILTER_PR)
                || filter.equals(Constants.FILTER_ISSUE)
                || filter.equals(Constants.FILTER_ANSWER)
                || filter.equals(Constants.FILTER_COMMENT)
                || filter.matches("^user=\\S+$");
    }
}
