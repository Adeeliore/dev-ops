package backend.academy.bot.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.constants.BotMessages;
import backend.academy.bot.dto.response.ListLinksResponse;
import backend.academy.bot.model.UserSession;
import backend.academy.bot.service.TrackingService;
import backend.academy.bot.service.UserSessionService;
import backend.academy.bot.service.impl.MessageSender;
import backend.academy.bot.state.UserState;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private TrackingService trackingService;

    @Mock
    private MessageSender messageSender;

    private CommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        commandHandler = new CommandHandler(userSessionService, trackingService, messageSender);
    }

    @Test
    @DisplayName("Команда /start - успешная регистрация")
    void shouldHandleStartCommand() {
        long chatId = 123L;
        when(trackingService.registerChat(chatId)).thenReturn(Mono.empty());

        commandHandler.handleCommand(chatId, "/start");

        verify(messageSender).sendMessage(chatId, BotMessages.START_MESSAGE);
    }

    @Test
    @DisplayName("Команда /help - вывод справки")
    void shouldHandleHelpCommand() {
        long chatId = 123L;

        commandHandler.handleCommand(chatId, "/help");

        verify(messageSender).sendMessage(chatId, BotMessages.HELP_MESSAGE);
    }

    @Test
    @DisplayName("Команда /track - запрос ссылки")
    void shouldHandleTrackCommand() {
        long chatId = 123L;
        UserSession session = new UserSession(chatId);
        when(userSessionService.getSession(chatId)).thenReturn(session);

        commandHandler.handleCommand(chatId, "/track");

        assertEquals(UserState.AWAITING_LINK, session.state());
        verify(messageSender).sendMessage(chatId, BotMessages.ENTER_LINK);
    }

    @Test
    @DisplayName("Команда /list - список ссылок")
    void shouldHandleListCommand() {
        long chatId = 123L;
        ListLinksResponse response = new ListLinksResponse(List.of(), 0);
        when(trackingService.listTrackedLinks(chatId)).thenReturn(Mono.just(response));

        commandHandler.handleCommand(chatId, "/list");

        verify(messageSender).sendMessage(chatId, "У вас пока нет отслеживаемых ссылок.");
    }

    @Test
    @DisplayName("Неизвестная команда")
    void shouldHandleUnknownCommand() {
        long chatId = 123L;

        commandHandler.handleCommand(chatId, "/unknown");

        verify(messageSender).sendMessage(chatId, BotMessages.UNKNOWN_COMMAND);
    }
}
