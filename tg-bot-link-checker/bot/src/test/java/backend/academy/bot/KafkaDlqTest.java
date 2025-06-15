package backend.academy.bot;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import backend.academy.bot.listener.KafkaUpdateListener;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@DirtiesContext
public class KafkaDlqTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.1"));

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaRawTemplate;

    @MockitoSpyBean
    private KafkaUpdateListener listener;

    @Test
    void whenInvalidMessage_thenDlqListenerIsInvoked() {
        kafkaRawTemplate.send("updates-topic", "key", "not-a-valid-json".getBytes(StandardCharsets.UTF_8));

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> verify(listener, atLeastOnce()).listenDlq("not-a-valid-json"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public KafkaTemplate<String, byte[]> kafkaRawTemplate(KafkaProperties properties) {
            ProducerFactory<String, byte[]> pf = new DefaultKafkaProducerFactory<>(
                    properties.buildProducerProperties(), new StringSerializer(), new ByteArraySerializer());
            return new KafkaTemplate<>(pf);
        }
    }
}
