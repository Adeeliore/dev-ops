package backend.academy.scrapper.config;

import backend.academy.scrapper.AppConfig;
import backend.academy.scrapper.logging.WebClientLoggingFilter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Setter
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final AppConfig appConfig;

    @Bean
    public WebClient webClient(WebClient.Builder builder, WebClientLoggingFilter loggingFilter) {
        return builder.baseUrl(appConfig.botBaseUrl())
                .filter(loggingFilter.logRequest())
                .filter(loggingFilter.logResponse())
                .filter(loggingFilter.logError())
                .build();
    }

    @Bean
    @Qualifier("githubWebClient")
    public WebClient githubWebClient(AppConfig config) {
        return WebClient.builder()
                .baseUrl(config.botBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + config.githubToken())
                .defaultHeader(HttpHeaders.USER_AGENT, "MyApp")
                .build();
    }

    @Bean
    @Qualifier("stackoverflowWebClient")
    public WebClient stackOverflowWebClient(AppConfig config) {
        return WebClient.builder()
                .baseUrl(config.stackOverflowApiUrl())
                .defaultHeader("X-API-Access-Token", config.stackOverflow().accessToken())
                .defaultHeader(HttpHeaders.USER_AGENT, "MyApp")
                .build();
    }
}
