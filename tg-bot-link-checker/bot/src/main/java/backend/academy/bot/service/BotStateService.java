package backend.academy.bot.service;

public interface BotStateService {
    String processMessage(long chatId, String message);
}
