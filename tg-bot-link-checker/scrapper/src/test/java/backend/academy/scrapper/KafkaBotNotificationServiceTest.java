package backend.academy.scrapper;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import backend.academy.scrapper.config.KafkaProperties;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.BotNotificationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestPropertySource(properties = {"app.message-transport=KAFKA"})
class KafkaBotNotificationServiceTest {

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.1"));

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @MockitoSpyBean
    private KafkaTemplate<String, LinkUpdate> kafkaTemplate;

    @Autowired
    private BotNotificationService botNotificationService;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Test
    void shouldSendKafkaMessageUsingKafkaTemplate() {
        LinkUpdate update =
                new LinkUpdate(1L, "https://github.com/test/repo", "Update: something changed", List.of(123L, 456L));

        botNotificationService.notifyUpdate(update);

        verify(kafkaTemplate, timeout(5000)).send(eq(kafkaProperties.topic().updates()), eq(update));
    }
}
