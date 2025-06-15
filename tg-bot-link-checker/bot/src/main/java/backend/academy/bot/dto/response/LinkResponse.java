package backend.academy.bot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public record LinkResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("url") String url,
        @JsonProperty("tags") Set<String> tags,
        @JsonProperty("filters") Set<String> filters) {}
