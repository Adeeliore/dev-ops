package backend.academy.bot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppConfig(String telegramToken, String scrapperBaseUrl, boolean includeStacktrace) {}
