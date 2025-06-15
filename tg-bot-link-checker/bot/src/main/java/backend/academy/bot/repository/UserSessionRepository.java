package backend.academy.bot.repository;

import backend.academy.bot.model.UserSession;

public interface UserSessionRepository {
    void save(UserSession session);

    UserSession getOrCreateSession(long chatId);

    void remove(long chatId);
}
