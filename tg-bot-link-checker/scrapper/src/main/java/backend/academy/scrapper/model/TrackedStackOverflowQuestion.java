package backend.academy.scrapper.model;

import java.time.Instant;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TrackedStackOverflowQuestion extends TrackedLink {
    private int answerCount;
    private int score;
    private int viewCount;

    public TrackedStackOverflowQuestion(
            long tgChatId,
            String url,
            Instant lastUpdated,
            int answerCount,
            int score,
            int viewCount,
            Set<String> tags,
            Set<String> filters) {
        super(tgChatId, url, lastUpdated, tags, filters);
        this.answerCount = answerCount;
        this.score = score;
        this.viewCount = viewCount;
    }
}
