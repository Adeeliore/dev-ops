package backend.academy.bot.client;

import backend.academy.bot.dto.request.AddLinkRequest;
import backend.academy.bot.dto.request.RemoveLinkRequest;
import backend.academy.bot.dto.response.LinkResponse;
import backend.academy.bot.dto.response.ListLinksResponse;
import reactor.core.publisher.Mono;

public interface ScrapperClient {
    Mono<Void> registerChat(long chatId);

    Mono<Void> deleteChat(long chatId);

    Mono<ListLinksResponse> getTrackedLinks(long chatId);

    Mono<LinkResponse> addLink(long chatId, AddLinkRequest request);

    Mono<LinkResponse> removeLink(long chatId, RemoveLinkRequest request);
}
