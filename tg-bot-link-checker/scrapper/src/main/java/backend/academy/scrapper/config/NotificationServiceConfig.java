package backend.academy.scrapper.config;

import backend.academy.scrapper.AppConfig;
import backend.academy.scrapper.constants.Constants;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.BotNotificationService;
import backend.academy.scrapper.service.impl.BotNotificationServiceImpl;
import backend.academy.scrapper.service.impl.KafkaBotNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class NotificationServiceConfig {

    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final AppConfig appConfig;

    @Bean
    public BotNotificationService botNotificationService() {
        String transport = appConfig.messageTransport();

        if (Constants.KAFKA_TRANSPORT.equalsIgnoreCase(transport)) {
            return new KafkaBotNotificationService(kafkaTemplate, kafkaProperties);
        } else if (Constants.HTTP_TRANSPORT.equalsIgnoreCase(transport)) {
            WebClient webClient =
                    webClientBuilder.baseUrl(appConfig.botBaseUrl()).build();
            return new BotNotificationServiceImpl(webClient);
        } else {
            throw new IllegalStateException("Unknown transport type: " + transport);
        }
    }
}
