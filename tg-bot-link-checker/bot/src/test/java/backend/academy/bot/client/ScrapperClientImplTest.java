package backend.academy.bot.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import backend.academy.bot.client.impl.ScrapperClientImpl;
import backend.academy.bot.dto.request.AddLinkRequest;
import backend.academy.bot.dto.request.RemoveLinkRequest;
import backend.academy.bot.dto.response.LinkResponse;
import backend.academy.bot.dto.response.ListLinksResponse;
import backend.academy.bot.exception.ScrapperApiException;
import backend.academy.bot.exception.response.ApiErrorResponse;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestPropertySource(properties = {"app.telegram-token=token"})
@ExtendWith(MockitoExtension.class)
public class ScrapperClientImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ScrapperClientImpl scrapperClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scrapperClient = new ScrapperClientImpl(webClient);
    }

    @Test
    void registerChat_SuccessfulResponse() {
        long chatId = 12345L;

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/tg-chat/{id}", chatId)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        StepVerifier.create(scrapperClient.registerChat(chatId)).verifyComplete();
    }

    @Test
    void deleteChat_SuccessfulResponse() {
        long chatId = 12345L;

        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/tg-chat/{id}", chatId)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        StepVerifier.create(scrapperClient.deleteChat(chatId)).verifyComplete();
    }

    @Test
    void getTrackedLinks_SuccessfulResponse() {
        long chatId = 12345L;
        ListLinksResponse mockResponse = new ListLinksResponse(
                List.of(
                        new LinkResponse(1L, "http://example.com", Set.of("tag1", "tag2"), Set.of("filter1")),
                        new LinkResponse(2L, "http://test.com", Set.of("tag3"), Set.of("filter2"))),
                2);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/links")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Tg-Chat-Id", String.valueOf(chatId))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ListLinksResponse.class)).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(scrapperClient.getTrackedLinks(chatId))
                .assertNext(response -> {
                    assertEquals(2, response.size());
                    assertEquals("http://example.com", response.links().get(0).url());
                    assertEquals(Set.of("tag1", "tag2"), response.links().get(0).tags());
                    assertEquals(Set.of("filter1"), response.links().get(0).filters());
                    assertEquals("http://test.com", response.links().get(1).url());
                    assertEquals(Set.of("tag3"), response.links().get(1).tags());
                    assertEquals(Set.of("filter2"), response.links().get(1).filters());
                })
                .verifyComplete();
    }

    @Test
    void addLink_SuccessfulResponse() {
        long chatId = 12345L;
        AddLinkRequest request = new AddLinkRequest("http://newlink.com", Set.of("tag1", "tag2"), Set.of("filter1"));
        LinkResponse mockResponse =
                new LinkResponse(3L, "http://newlink.com", Set.of("tag1", "tag2"), Set.of("filter1"));

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/links")).thenReturn(requestBodySpec);
        when(requestBodySpec.header("Tg-Chat-Id", String.valueOf(chatId))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(LinkResponse.class)).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(scrapperClient.addLink(chatId, request))
                .assertNext(response -> {
                    assertEquals(3L, response.id());
                    assertEquals("http://newlink.com", response.url());
                    assertEquals(Set.of("tag1", "tag2"), response.tags());
                    assertEquals(Set.of("filter1"), response.filters());
                })
                .verifyComplete();
    }

    @Test
    void removeLink_SuccessfulResponse() {
        long chatId = 12345L;
        RemoveLinkRequest request = new RemoveLinkRequest("http://removelink.com");
        LinkResponse mockResponse = new LinkResponse(4L, "http://removelink.com", Set.of("tag1"), Set.of("filter1"));

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/links")).thenReturn(requestBodySpec);
        when(requestBodySpec.header("Tg-Chat-Id", String.valueOf(chatId))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(LinkResponse.class)).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(scrapperClient.removeLink(chatId, request))
                .assertNext(response -> {
                    assertEquals(4L, response.id());
                    assertEquals("http://removelink.com", response.url());
                    assertEquals(Set.of("tag1"), response.tags());
                    assertEquals(Set.of("filter1"), response.filters());
                })
                .verifyComplete();
    }

    @Test
    void handleError_ErrorResponse() {
        long chatId = 12345L;
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                "Некорректные параметры запроса",
                "400_BAD_REQUEST",
                "IllegalArgumentException",
                "Invalid request data",
                List.of("StackTraceLine1", "StackTraceLine2"));
        ScrapperApiException expectedException = new ScrapperApiException(apiErrorResponse);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/tg-chat/{id}", chatId)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(expectedException));

        StepVerifier.create(scrapperClient.registerChat(chatId))
                .expectErrorMatches(throwable -> throwable instanceof ScrapperApiException
                        && throwable.getMessage().contains("Некорректные параметры запроса"))
                .verify();
    }
}
