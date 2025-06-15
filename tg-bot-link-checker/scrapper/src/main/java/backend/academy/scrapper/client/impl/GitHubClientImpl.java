package backend.academy.scrapper.client.impl;

import backend.academy.scrapper.AppConfig;
import backend.academy.scrapper.client.AbstractApiClient;
import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.dto.client.Issue;
import backend.academy.scrapper.dto.client.PullRequest;
import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GitHubClientImpl extends AbstractApiClient implements GitHubClient {

    private final String gitHubApiUrl;

    public GitHubClientImpl(@Qualifier("githubWebClient") WebClient webClient, AppConfig appConfig) {
        super(webClient);
        this.gitHubApiUrl = appConfig.githubApiUrl();
    }

    @Override
    public Mono<GitHubRepoResponse> fetchRepoInfo(String owner, String repo) {
        Map<String, Object> params = Map.of("owner", owner, "repo", repo);
        return fetchData(gitHubApiUrl, params, GitHubRepoResponse.class);
    }

    @Override
    public Mono<List<PullRequest>> fetchPullRequests(String owner, String repo, Instant since) {
        String uriTemplate = gitHubApiUrl + "/pulls?state=all&sort=updated&direction=desc";
        Map<String, Object> params = new HashMap<>();
        params.put("owner", owner);
        params.put("repo", repo);
        if (since != null) {
            params.put("since", since.toString());
            uriTemplate += "&since={since}";
        }
        return fetchData(uriTemplate, params, new ParameterizedTypeReference<List<PullRequest>>() {});
    }

    @Override
    public Mono<List<Issue>> fetchIssues(String owner, String repo, Instant since) {
        String uriTemplate = gitHubApiUrl + "/issues?state=all&sort=updated&direction=desc";
        Map<String, Object> params = new HashMap<>();
        params.put("owner", owner);
        params.put("repo", repo);
        if (since != null) {
            params.put("since", since.toString());
            uriTemplate += "&since={since}";
        }
        return fetchData(uriTemplate, params, new ParameterizedTypeReference<List<Issue>>() {});
    }
}
