package backend.academy.bot.service;

import backend.academy.bot.model.UserSession;

public interface UserSessionService {
    UserSession getSession(long chatId);

    void resetSession(long chatId);

    void saveSession(UserSession session);
}
