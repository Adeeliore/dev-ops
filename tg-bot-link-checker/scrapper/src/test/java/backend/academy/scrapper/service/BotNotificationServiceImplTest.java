package backend.academy.scrapper.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.impl.BotNotificationServiceImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class BotNotificationServiceImplTest {
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private BotNotificationServiceImpl botNotificationService;

    @BeforeEach
    void setUp() {
        botNotificationService = new BotNotificationServiceImpl(webClient);
    }

    @Test
    void shouldNotifyUpdate() {
        LinkUpdate linkUpdate = new LinkUpdate(
                123L, "https://github.com/test/repo", "New PR in repo: Test PR by test-user", List.of(456L));

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/updates")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> botNotificationService.notifyUpdate(linkUpdate));

        verify(webClient).post();
        verify(requestBodyUriSpec).uri("/updates");
        verify(requestBodySpec).bodyValue(linkUpdate);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).toBodilessEntity();
    }
}
