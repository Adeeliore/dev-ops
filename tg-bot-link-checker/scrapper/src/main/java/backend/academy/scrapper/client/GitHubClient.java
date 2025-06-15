package backend.academy.scrapper.client;

import backend.academy.scrapper.dto.client.Issue;
import backend.academy.scrapper.dto.client.PullRequest;
import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import java.time.Instant;
import java.util.List;
import reactor.core.publisher.Mono;

public interface GitHubClient {
    Mono<GitHubRepoResponse> fetchRepoInfo(String owner, String repo);

    Mono<List<PullRequest>> fetchPullRequests(String owner, String repo, Instant since);

    Mono<List<Issue>> fetchIssues(String owner, String repo, Instant since);
}
