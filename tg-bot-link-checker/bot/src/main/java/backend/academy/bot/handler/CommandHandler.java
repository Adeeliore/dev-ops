package backend.academy.bot.handler;

import backend.academy.bot.constants.BotMessages;
import backend.academy.bot.dto.response.LinkResponse;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.service.*;
import backend.academy.bot.service.impl.MessageSender;
import backend.academy.bot.state.UserState;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final UserSessionService userSessionService;
    private final TrackingService trackingService;
    private final MessageSender messageSender;

    public void handleCommand(long chatId, String message) {
        UserSession session = userSessionService.getSession(chatId);
        String[] parts = message.split("\\s+");

        switch (parts[0]) {
            case "/start" -> handleStart(chatId);
            case "/help" -> handleHelp(chatId);
            case "/track" -> handleTrack(chatId, session);
            case "/untrack" -> handleUntrack(chatId, message);
            case "/list" -> handleList(chatId);
            default -> messageSender.sendMessage(chatId, BotMessages.UNKNOWN_COMMAND);
        }
    }

    private void handleStart(long chatId) {
        trackingService
                .registerChat(chatId)
                .then(Mono.fromRunnable(() -> messageSender.sendMessage(chatId, BotMessages.START_MESSAGE)))
                .doOnError(
                        error -> messageSender.sendMessage(chatId, BotMessages.REGISTRATION_ERROR + error.getMessage()))
                .subscribe();
    }

    private void handleHelp(long chatId) {
        messageSender.sendMessage(chatId, BotMessages.HELP_MESSAGE);
    }

    private void handleTrack(long chatId, UserSession session) {
        session.state(UserState.AWAITING_LINK);
        userSessionService.saveSession(session);
        messageSender.sendMessage(chatId, BotMessages.ENTER_LINK);
    }

    private void handleUntrack(long chatId, String message) {
        String[] parts = message.split("\\s+");
        if (parts.length < 2) {
            messageSender.sendMessage(chatId, BotMessages.UNTRACK_NO_LINK);
            return;
        }

        String link = parts[1];
        trackingService
                .untrackLink(chatId, link)
                .subscribe(success -> messageSender.sendMessage(
                        chatId, success ? BotMessages.LINK_REMOVED : BotMessages.LINK_NOT_FOUND));
    }

    private void handleList(long chatId) {
        trackingService.listTrackedLinks(chatId).subscribe(response -> {
            if (response.links().isEmpty()) {
                messageSender.sendMessage(chatId, BotMessages.NO_TRACKED_LINKS);
            } else {
                String links = response.links().stream().map(LinkResponse::url).collect(Collectors.joining("\n"));
                messageSender.sendMessage(chatId, BotMessages.TRACKED_LINKS_PREFIX + links);
            }
        });
    }
}
