package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.BotNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class BotNotificationServiceImpl implements BotNotificationService {
    private final WebClient webClient;

    @Override
    public void notifyUpdate(LinkUpdate linkUpdate) {
        webClient
                .post()
                .uri("/updates")
                .bodyValue(linkUpdate)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }
}
