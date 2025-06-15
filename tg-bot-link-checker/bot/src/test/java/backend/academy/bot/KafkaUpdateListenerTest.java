package backend.academy.bot;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.impl.MessageSender;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class KafkaUpdateListenerTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.1")).withExposedPorts(9092);

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("kafka.topic.updates", () -> "update-topic");
        registry.add("kafka.topic.dlq", () -> "dlq-topic");
    }

    @Autowired
    private KafkaTemplate<String, LinkUpdate> kafkaTemplate;

    @MockitoBean
    private MessageSender messageSender;

    @Test
    void shouldConsumeUpdateMessageAndInvokeMessageSender() {
        LinkUpdate update = new LinkUpdate(1L, "https://example.com", "Новое описание", List.of(1L, 2L));

        kafkaTemplate.send("update-topic", update);
        kafkaTemplate.flush();

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            verify(messageSender, times(2)).sendMessage(anyLong(), contains("https://example.com"));
        });
    }

    @TestConfiguration
    public static class KafkaTemplateTestConfig {

        @Bean
        public KafkaTemplate<String, LinkUpdate> testKafkaTemplate(KafkaProperties kafkaProperties) {
            Map<String, Object> producerProps = new HashMap<>(kafkaProperties.buildProducerProperties());
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

            DefaultKafkaProducerFactory<String, LinkUpdate> pf = new DefaultKafkaProducerFactory<>(producerProps);

            return new KafkaTemplate<>(pf);
        }
    }
}
