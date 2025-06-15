package backend.academy.bot.service;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.impl.MessageSender;
import backend.academy.bot.service.impl.UpdateService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateServiceTest {

    @Mock
    private MessageSender messageSender;

    @InjectMocks
    private UpdateService updateService;

    @Test
    void processUpdate_SendsMessagesToAllChatIds() {
        List<Long> chatIds = List.of(12345L, 67890L);
        LinkUpdate update = new LinkUpdate(1L, "http://example.com", "Update description", chatIds);
        String expectedMessage = "🔔 Обновление по ссылке:\nhttp://example.com\n\n📌 Update description";
        doNothing().when(messageSender).sendMessage(anyLong(), anyString());

        updateService.processUpdate(update);

        verify(messageSender).sendMessage(12345L, expectedMessage);
        verify(messageSender).sendMessage(67890L, expectedMessage);
        verifyNoMoreInteractions(messageSender);
    }

    @Test
    void processUpdate_EmptyChatIds_DoesNothing() {
        LinkUpdate update = new LinkUpdate(1L, "http://example.com", "Update description", Collections.emptyList());

        updateService.processUpdate(update);

        verifyNoInteractions(messageSender);
    }
}
