package backend.academy.scrapper.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.AppConfig;
import backend.academy.scrapper.client.impl.StackOverflowClientImpl;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import backend.academy.scrapper.exception.ExternalServiceException;
import backend.academy.scrapper.repository.BaseTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@EnableConfigurationProperties(AppConfig.class)
@ExtendWith(MockitoExtension.class)
public class StackOverflowClientImplTest extends BaseTest {

    @Autowired
    private AppConfig appConfig;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private StackOverflowClientImpl stackOverflowClient;

    private static final String URL =
            "https://api.stackexchange.com/2.3/questions/{id}?order=desc&sort=activity&site=stackoverflow&filter=!9_bDE(fI5)&key={key}&id={id}";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stackOverflowClient = new StackOverflowClientImpl(webClient, appConfig);
    }

    @Test
    @DisplayName("Тест успешного получения информации о вопросе StackOverflow")
    void fetchQuestionInfo_SuccessfulResponse() {
        long questionId = 123456L;

        StackOverflowQuestionResponse mockResponse =
                new StackOverflowQuestionResponse(List.of(new StackOverflowQuestionResponse.QuestionItem(
                        questionId,
                        "How to test WebClient?",
                        "https://stackoverflow.com/questions/123456",
                        1672531200L,
                        3,
                        10,
                        150)));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                        eq(URL), eq(Map.of("key", appConfig.stackOverflow().key(), "id", questionId))))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(StackOverflowQuestionResponse.class)).thenReturn(Mono.just(mockResponse));

        Mono<StackOverflowQuestionResponse> responseMono = stackOverflowClient.fetchQuestionInfo(questionId);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(1, response.items().size());
                    StackOverflowQuestionResponse.QuestionItem item =
                            response.items().get(0);
                    assertEquals(questionId, item.questionId());
                    assertEquals("How to test WebClient?", item.title());
                    assertEquals("https://stackoverflow.com/questions/123456", item.link());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Тест обработки ошибки клиента при запросе StackOverflow API")
    void fetchQuestionInfo_ClientError() {
        long questionId = 123456L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                        eq(URL), eq(Map.of("key", appConfig.stackOverflow().key(), "id", questionId))))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(StackOverflowQuestionResponse.class))
                .thenReturn(Mono.error(new ExternalServiceException("StackOverflow API вернул ошибку: 404")));

        StepVerifier.create(stackOverflowClient.fetchQuestionInfo(questionId))
                .expectErrorMatches(throwable -> throwable instanceof ExternalServiceException
                        && throwable.getMessage().contains("StackOverflow API вернул ошибку"))
                .verify();
    }

    @Test
    @DisplayName("Тест обработки серверной ошибки при запросе StackOverflow API")
    void fetchQuestionInfo_ServerError() {
        long questionId = 123456L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                        eq(URL), eq(Map.of("key", appConfig.stackOverflow().key(), "id", questionId))))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(StackOverflowQuestionResponse.class))
                .thenReturn(Mono.error(new ExternalServiceException("StackOverflow API временно недоступен: 500")));

        StepVerifier.create(stackOverflowClient.fetchQuestionInfo(questionId))
                .expectErrorMatches(throwable -> throwable instanceof ExternalServiceException
                        && throwable.getMessage().contains("StackOverflow API временно недоступен"))
                .verify();
    }
}
