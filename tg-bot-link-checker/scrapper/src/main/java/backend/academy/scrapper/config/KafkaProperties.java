package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(@NotEmpty String groupId, @NotNull Topics topic) {
    public record Topics(@NotEmpty String updates, @NotEmpty String dlq) {}
}
