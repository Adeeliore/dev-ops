package backend.academy.scrapper.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record Issue(String title, User user, @JsonProperty("created_at") Instant createdAt, String body) {
    public String generateMessage(String repo) {
        String bodyText = body != null ? body.substring(0, Math.min(200, body.length())) : "";
        return "New Issue in " + repo + ": " + title + " by " + user.login() + " at " + createdAt + "\n" + bodyText;
    }
}
