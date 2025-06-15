package backend.academy.scrapper.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubRepoResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("html_url") String url,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("pushed_at") String pushedAt,
        @JsonProperty("open_issues_count") int openIssues,
        @JsonProperty("stargazers_count") int stars,
        @JsonProperty("forks_count") int forks) {}
