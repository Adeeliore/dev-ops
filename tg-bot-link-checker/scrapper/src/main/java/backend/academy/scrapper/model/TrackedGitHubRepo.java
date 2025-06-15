package backend.academy.scrapper.model;

import java.time.Instant;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TrackedGitHubRepo extends TrackedLink {
    private int openIssues;
    private int stars;
    private int forks;

    public TrackedGitHubRepo(
            long tgChatId,
            String url,
            Instant lastUpdated,
            int openIssues,
            int stars,
            int forks,
            Set<String> tags,
            Set<String> filters) {
        super(tgChatId, url, lastUpdated, tags, filters);
        this.openIssues = openIssues;
        this.stars = stars;
        this.forks = forks;
    }
}
