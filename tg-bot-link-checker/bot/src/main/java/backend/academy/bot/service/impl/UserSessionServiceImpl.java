package backend.academy.bot.service.impl;

import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import backend.academy.bot.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {
    private final UserSessionRepository repository;

    @Override
    public UserSession getSession(long chatId) {
        return repository.getOrCreateSession(chatId);
    }

    @Override
    public void resetSession(long chatId) {
        repository.save(new UserSession(chatId));
    }

    @Override
    public void saveSession(UserSession session) {
        repository.save(session);
    }
}
