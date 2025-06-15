package backend.academy.scrapper.dto.client;

import java.time.Instant;

public record Comment(String user, Instant createdAt, String body) {
    public String generateMessage() {
        String bodyText = body != null ? body.substring(0, Math.min(200, body.length())) : "";
        return "New comment by " + user + " at " + createdAt + "\n" + bodyText;
    }
}
