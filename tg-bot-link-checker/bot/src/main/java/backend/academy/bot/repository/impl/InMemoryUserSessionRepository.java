package backend.academy.bot.repository.impl;

import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserSessionRepository implements UserSessionRepository {
    private final Map<Long, UserSession> sessionMap = new HashMap<>();

    public void save(UserSession session) {
        sessionMap.put(session.chatId(), session);
    }

    public UserSession getOrCreateSession(long chatId) {
        return sessionMap.computeIfAbsent(chatId, UserSession::new);
    }

    public void remove(long chatId) {
        sessionMap.remove(chatId);
    }
}
