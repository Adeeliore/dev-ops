package backend.academy.bot.service.impl;

import backend.academy.bot.handler.CommandHandler;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.service.BotStateService;
import backend.academy.bot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramBotService {
    private final TelegramBot bot;
    private final CommandHandler commandHandler;
    private final BotStateService stateHandler;
    private final UserSessionService userSessionService;
    private final MessageSender messageSender;

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::handleUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void handleUpdate(Update update) {
        if (update.message() == null || update.message().text() == null) return;

        long chatId = update.message().chat().id();
        String messageText = update.message().text();

        UserSession session = userSessionService.getSession(chatId);

        if (session.state().isWaitingForInput()) {
            String response = stateHandler.processMessage(chatId, messageText);
            messageSender.sendMessage(chatId, response);
        } else {
            commandHandler.handleCommand(chatId, messageText);
        }
    }
}
