package backend.academy.bot.service.impl;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.dto.request.AddLinkRequest;
import backend.academy.bot.dto.request.RemoveLinkRequest;
import backend.academy.bot.dto.response.LinkResponse;
import backend.academy.bot.dto.response.ListLinksResponse;
import backend.academy.bot.service.TrackingService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {
    private final ScrapperClient scrapperClient;

    @Override
    public Mono<Void> registerChat(long chatId) {
        return scrapperClient.registerChat(chatId);
    }

    @Override
    public Mono<Void> deleteChat(long chatId) {
        return scrapperClient.deleteChat(chatId);
    }

    @Override
    public Mono<LinkResponse> trackLink(long chatId, String url, Set<String> tags, Set<String> filters) {
        return scrapperClient.addLink(chatId, new AddLinkRequest(url, tags, filters));
    }

    @Override
    public Mono<Boolean> untrackLink(long chatId, String url) {
        return scrapperClient
                .removeLink(chatId, new RemoveLinkRequest(url))
                .thenReturn(true)
                .onErrorReturn(false);
    }

    @Override
    public Mono<ListLinksResponse> listTrackedLinks(long chatId) {
        return scrapperClient.getTrackedLinks(chatId);
    }
}
