package backend.academy.bot.client.impl;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.dto.request.AddLinkRequest;
import backend.academy.bot.dto.request.RemoveLinkRequest;
import backend.academy.bot.dto.response.LinkResponse;
import backend.academy.bot.dto.response.ListLinksResponse;
import backend.academy.bot.exception.ScrapperApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ScrapperClientImpl implements ScrapperClient {
    private final WebClient webClient;
    private final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    @Override
    public Mono<Void> registerChat(long chatId) {
        return webClient
                .post()
                .uri("/tg-chat/{id}", chatId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> deleteChat(long chatId) {
        return webClient
                .delete()
                .uri("/tg-chat/{id}", chatId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(Void.class);
    }

    @Override
    @Cacheable(value = "tracked-links", key = "#chatId")
    public Mono<ListLinksResponse> getTrackedLinks(long chatId) {
        return webClient
                .get()
                .uri("/links")
                .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(ListLinksResponse.class);
    }

    @Override
    @CacheEvict(value = "tracked-links", key = "#chatId")
    public Mono<LinkResponse> addLink(long chatId, AddLinkRequest request) {
        return webClient
                .post()
                .uri("/links")
                .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(LinkResponse.class);
    }

    @Override
    @CacheEvict(value = "tracked-links", key = "#chatId")
    public Mono<LinkResponse> removeLink(long chatId, RemoveLinkRequest request) {
        return webClient
                .method(HttpMethod.DELETE)
                .uri("/links")
                .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(LinkResponse.class);
    }

    private Mono<? extends Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(ScrapperApiException.class).flatMap(Mono::error);
    }
}
