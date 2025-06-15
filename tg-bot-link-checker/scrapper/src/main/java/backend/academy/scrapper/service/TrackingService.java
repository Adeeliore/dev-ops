package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;

public interface TrackingService {
    void registerChat(long chatId);

    void deleteChat(long chatId);

    LinkResponse addLink(long chatId, AddLinkRequest request);

    LinkResponse removeLink(long chatId, RemoveLinkRequest request);

    ListLinksResponse getTrackedLinks(long chatId);
}
