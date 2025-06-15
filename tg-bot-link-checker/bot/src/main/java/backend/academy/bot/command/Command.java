package backend.academy.bot.command;

import backend.academy.bot.model.UserSession;

public interface Command {
    String execute(long chatId, String message, UserSession session);
}
