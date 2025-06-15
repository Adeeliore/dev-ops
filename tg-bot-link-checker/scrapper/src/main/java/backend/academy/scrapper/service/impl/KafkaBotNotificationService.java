package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.config.KafkaProperties;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.BotNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaBotNotificationService implements BotNotificationService {
    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    @Override
    public void notifyUpdate(LinkUpdate linkUpdate) {
        kafkaTemplate
                .send(kafkaProperties.topic().updates(), linkUpdate)
                .thenAccept(result -> log.info(
                        "✅ Sent update to Kafka topic [{}]: {}",
                        kafkaProperties.topic().updates(),
                        linkUpdate))
                .exceptionally(ex -> {
                    log.error(
                            "❌ Failed to send update to Kafka topic [{}]: {}",
                            kafkaProperties.topic().updates(),
                            linkUpdate,
                            ex);
                    return null;
                });
    }
}
