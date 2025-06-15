package backend.academy.bot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.command.BackCommand;
import backend.academy.bot.command.FiltersCommand;
import backend.academy.bot.command.LinkCommand;
import backend.academy.bot.command.TagsCommand;
import backend.academy.bot.command.UnknownCommand;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.repository.UserSessionRepository;
import backend.academy.bot.service.impl.BotStateServiceImpl;
import backend.academy.bot.state.UserState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BotStateServiceImplTest {

    @Mock
    private UserSessionRepository sessionRepository;

    @Mock
    private LinkCommand linkCommand;

    @Mock
    private TagsCommand tagsCommand;

    @Mock
    private FiltersCommand filtersCommand;

    @Mock
    private BackCommand backCommand;

    @Mock
    private UnknownCommand unknownCommand;

    @InjectMocks
    private BotStateServiceImpl botStateService;

    private final long chatId = 123456L;
    private final String message = "тестовое сообщение";

    private UserSession mockSession(UserState state, boolean stubState) {
        UserSession session = Mockito.mock(UserSession.class);
        if (stubState) {
            when(session.state()).thenReturn(state);
        }
        return session;
    }

    @Test
    @DisplayName("Возврат назад: должен вызвать backCommand и вернуть его результат")
    void shouldExecuteBackCommandWhenMessageIsBack() {
        UserSession session = mockSession(UserState.AWAITING_TAGS, false);
        when(sessionRepository.getOrCreateSession(chatId)).thenReturn(session);
        when(backCommand.execute(chatId, "назад", session)).thenReturn("back result");

        String result = botStateService.processMessage(chatId, "назад");

        assertEquals("back result", result);
        verify(backCommand).execute(chatId, "назад", session);
    }

    @Test
    @DisplayName("Состояние AWAITING_LINK: должен вызвать linkCommand")
    void shouldProcessAwaitingLinkState() {
        UserSession session = mockSession(UserState.AWAITING_LINK, true);
        when(sessionRepository.getOrCreateSession(chatId)).thenReturn(session);
        when(linkCommand.execute(chatId, message, session)).thenReturn("link result");

        String result = botStateService.processMessage(chatId, message);

        assertEquals("link result", result);
        verify(linkCommand).execute(chatId, message, session);
    }

    @Test
    @DisplayName("Состояние AWAITING_TAGS: должен вызвать tagsCommand")
    void shouldProcessAwaitingTagsState() {
        UserSession session = mockSession(UserState.AWAITING_TAGS, true);
        when(sessionRepository.getOrCreateSession(chatId)).thenReturn(session);
        when(tagsCommand.execute(chatId, message, session)).thenReturn("tags result");

        String result = botStateService.processMessage(chatId, message);

        assertEquals("tags result", result);
        verify(tagsCommand).execute(chatId, message, session);
    }

    @Test
    @DisplayName("Состояние AWAITING_FILTERS: должен вызвать filtersCommand")
    void shouldProcessAwaitingFiltersState() {
        UserSession session = mockSession(UserState.AWAITING_FILTERS, true);
        when(sessionRepository.getOrCreateSession(chatId)).thenReturn(session);
        when(filtersCommand.execute(chatId, message, session)).thenReturn("filters result");

        String result = botStateService.processMessage(chatId, message);

        assertEquals("filters result", result);
        verify(filtersCommand).execute(chatId, message, session);
    }

    @Test
    @DisplayName("Состояние NONE: должен вызвать unknownCommand")
    void shouldProcessUnknownState() {
        UserSession session = mockSession(UserState.NONE, true);
        when(sessionRepository.getOrCreateSession(chatId)).thenReturn(session);
        when(unknownCommand.execute(chatId, message, session)).thenReturn("unknown result");

        String result = botStateService.processMessage(chatId, message);

        assertEquals("unknown result", result);
        verify(unknownCommand).execute(chatId, message, session);
    }
}
