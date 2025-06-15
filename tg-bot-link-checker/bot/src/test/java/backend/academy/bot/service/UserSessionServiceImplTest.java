package backend.academy.bot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import backend.academy.bot.service.impl.UserSessionServiceImpl;
import backend.academy.bot.state.UserState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserSessionServiceImplTest {

    @Mock
    private UserSessionRepository repository;

    @InjectMocks
    private UserSessionServiceImpl userSessionService;

    private static final long CHAT_ID = 12345L;

    @BeforeEach
    void setUp() {
        reset(repository);
    }

    @Test
    void getSession_ReturnsSessionFromRepository() {
        UserSession expectedSession = new UserSession(CHAT_ID);
        when(repository.getOrCreateSession(CHAT_ID)).thenReturn(expectedSession);

        UserSession actualSession = userSessionService.getSession(CHAT_ID);

        assertEquals(expectedSession, actualSession);
        verify(repository).getOrCreateSession(CHAT_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void resetSession_SavesNewSessionWithChatId() {
        doNothing().when(repository).save(any(UserSession.class));

        userSessionService.resetSession(CHAT_ID);

        verify(repository)
                .save(argThat(session -> session.chatId() == CHAT_ID
                        && session.state() == UserState.NONE
                        && session.pendingLink() == null
                        && session.pendingTags().isEmpty()
                        && session.pendingFilters().isEmpty()));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void saveSession_SavesProvidedSession() {
        UserSession session = new UserSession(CHAT_ID);
        session.state(UserState.AWAITING_LINK);
        session.pendingLink("http://example.com");
        doNothing().when(repository).save(session);

        userSessionService.saveSession(session);

        verify(repository).save(session);
        verifyNoMoreInteractions(repository);
    }
}
