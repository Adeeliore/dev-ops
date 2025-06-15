package backend.academy.scrapper.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public record AddLinkRequest(
        @JsonProperty("link") String link,
        @JsonProperty("tags") Set<String> tags,
        @JsonProperty("filters") Set<String> filters) {}
