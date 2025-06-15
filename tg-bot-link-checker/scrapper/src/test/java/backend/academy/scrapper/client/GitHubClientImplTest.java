package backend.academy.scrapper.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.AppConfig;
import backend.academy.scrapper.client.impl.GitHubClientImpl;
import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import backend.academy.scrapper.exception.ExternalServiceException;
import backend.academy.scrapper.repository.BaseTest;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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
public class GitHubClientImplTest extends BaseTest {

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

    private GitHubClientImpl gitHubClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitHubClient = new GitHubClientImpl(webClient, appConfig);
    }

    @Test
    void fetchRepoInfo_SuccessfulResponse() {
        String owner = "octocat";
        String repo = "Hello-World";

        GitHubRepoResponse mockResponse = new GitHubRepoResponse(
                1296269L,
                "Hello-World",
                "https://github.com/octocat/Hello-World",
                "2023-01-01T12:00:00Z",
                "2023-01-01T12:05:00Z",
                5,
                42,
                10);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                        eq("https://api.github.com/repos/{owner}/{repo}"), eq(Map.of("owner", owner, "repo", repo))))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GitHubRepoResponse.class)).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(gitHubClient.fetchRepoInfo(owner, repo))
                .assertNext(response -> {
                    assertEquals(1296269L, response.id());
                    assertEquals("Hello-World", response.name());
                    assertEquals("https://github.com/octocat/Hello-World", response.url());
                })
                .verifyComplete();
    }

    @Test
    void fetchRepoInfo_ClientError() {
        String owner = "octocat";
        String repo = "Hello-World";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                        eq("https://api.github.com/repos/{owner}/{repo}"), eq(Map.of("owner", owner, "repo", repo))))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GitHubRepoResponse.class))
                .thenReturn(Mono.error(new ExternalServiceException("GitHub API вернул ошибку: 404")));

        StepVerifier.create(gitHubClient.fetchRepoInfo(owner, repo))
                .expectErrorMatches(throwable -> throwable instanceof ExternalServiceException
                        && throwable.getMessage().contains("GitHub API вернул ошибку"))
                .verify();
    }

    @Test
    void fetchRepoInfo_ServerError() {
        String owner = "octocat";
        String repo = "Hello-World";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(
                        eq("https://api.github.com/repos/{owner}/{repo}"), eq(Map.of("owner", owner, "repo", repo))))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GitHubRepoResponse.class))
                .thenReturn(Mono.error(new ExternalServiceException("GitHub API временно недоступен: 500")));

        StepVerifier.create(gitHubClient.fetchRepoInfo(owner, repo))
                .expectErrorMatches(throwable -> throwable instanceof ExternalServiceException
                        && throwable.getMessage().contains("GitHub API временно недоступен"))
                .verify();
    }
}
