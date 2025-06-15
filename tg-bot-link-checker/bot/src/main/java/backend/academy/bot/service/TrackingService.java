package backend.academy.bot.service;

import backend.academy.bot.dto.response.LinkResponse;
import backend.academy.bot.dto.response.ListLinksResponse;
import java.util.Set;
import reactor.core.publisher.Mono;

public interface TrackingService {
    Mono<Void> registerChat(long chatId);

    Mono<Void> deleteChat(long chatId);

    Mono<LinkResponse> trackLink(long chatId, String url, Set<String> tags, Set<String> filters);

    Mono<Boolean> untrackLink(long chatId, String url);

    Mono<ListLinksResponse> listTrackedLinks(long chatId);
}
