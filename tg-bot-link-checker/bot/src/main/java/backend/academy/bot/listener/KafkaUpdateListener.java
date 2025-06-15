package backend.academy.bot.listener;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.impl.UpdateService;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
public class KafkaUpdateListener {

    private final UpdateService updateService;
    private final KafkaProperties kafkaProperties;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final CountDownLatch dlqLatch = new CountDownLatch(1);

    @KafkaListener(topics = "#{'${kafka.topic.updates}'}")
    public void listen(LinkUpdate update) {
        log.info("Received update from Kafka: {}", update);
        updateService.processUpdate(update);
        latch.countDown();
    }

    @KafkaListener(topics = "#{'${kafka.topic.dlq}'}")
    public void listenDlq(String deadMessage) {
        log.warn("💀Received message in DLQ: {}", deadMessage);
        dlqLatch.countDown();
    }
}
