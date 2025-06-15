package backend.academy.bot.config;

import backend.academy.bot.AppConfig;
import backend.academy.bot.logging.WebClientLoggingFilter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Setter
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final AppConfig appConfig;

    @Bean
    public WebClient webClient(WebClient.Builder builder, WebClientLoggingFilter loggingFilter) {
        return builder.baseUrl(appConfig.scrapperBaseUrl())
                .filter(loggingFilter.logRequest())
                .filter(loggingFilter.logResponse())
                .filter(loggingFilter.logError())
                .build();
    }
}
