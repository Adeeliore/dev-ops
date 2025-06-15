package backend.academy.bot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSender {
    private final TelegramBot bot;

    public void sendMessage(Long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }
}
