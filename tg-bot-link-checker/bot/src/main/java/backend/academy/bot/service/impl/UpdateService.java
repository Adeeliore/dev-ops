package backend.academy.bot.service.impl;

import backend.academy.bot.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final MessageSender messageSender;

    public void processUpdate(LinkUpdate update) {
        for (Long chatId : update.tgChatIds()) {
            String message = "🔔 Обновление по ссылке:\n" + update.url() + "\n\n📌 " + update.description();
            messageSender.sendMessage(chatId, message);
        }
    }
}
