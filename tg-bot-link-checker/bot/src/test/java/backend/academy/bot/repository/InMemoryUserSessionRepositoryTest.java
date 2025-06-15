package backend.academy.bot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.impl.InMemoryUserSessionRepository;
import backend.academy.bot.state.UserState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryUserSessionRepositoryTest {

    private final InMemoryUserSessionRepository repository = new InMemoryUserSessionRepository();

    @Test
    @DisplayName("Создание и извлечение новой сессии")
    void shouldCreateAndRetrieveSession() {
        long chatId = 123L;
        UserSession session = repository.getOrCreateSession(chatId);

        assertNotNull(session);
        assertEquals(chatId, session.chatId());
        assertEquals(UserState.NONE, session.state());
    }

    @Test
    @DisplayName("Сохранение состояния сессии")
    void shouldSaveSessionState() {
        long chatId = 123L;
        UserSession session = repository.getOrCreateSession(chatId);
        session.state(UserState.AWAITING_LINK);
        repository.save(session);

        UserSession retrieved = repository.getOrCreateSession(chatId);
        assertEquals(UserState.AWAITING_LINK, retrieved.state());
    }

    @Test
    @DisplayName("Удаление сессии")
    void shouldRemoveSession() {
        long chatId = 123L;
        repository.getOrCreateSession(chatId);
        repository.remove(chatId);

        assertEquals(UserState.NONE, repository.getOrCreateSession(chatId).state());
    }
}
