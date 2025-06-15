package backend.academy.scrapper.controller;

import backend.academy.scrapper.api.ScrapperApi;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScrapperController implements ScrapperApi {
    private final TrackingService trackingService;

    @Override
    public void registerChat(long id) {
        trackingService.registerChat(id);
    }

    @Override
    public void deleteChat(long id) {
        trackingService.deleteChat(id);
    }

    @Override
    public ListLinksResponse getTrackedLinks(long chatId) {
        return trackingService.getTrackedLinks(chatId);
    }

    @Override
    public LinkResponse addLink(long chatId, AddLinkRequest request) {
        return trackingService.addLink(chatId, request);
    }

    @Override
    public LinkResponse removeLink(long chatId, RemoveLinkRequest request) {
        return trackingService.removeLink(chatId, request);
    }
}
