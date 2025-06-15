package backend.academy.scrapper.model;

import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class TrackedLink {
    private final long tgChatId;
    private final String url;
    private Instant lastUpdated;
    private Set<String> tags;
    private Set<String> filters;
}
