package backend.academy.scrapper;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record AppConfig(
        @NotEmpty String githubToken,
        @NotNull StackOverflowCredentials stackOverflow,
        @NotEmpty String botBaseUrl,
        @NotEmpty String githubApiUrl,
        @NotEmpty String stackOverflowApiUrl,
        boolean includeStacktrace,
        String messageTransport) {
    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}
}
