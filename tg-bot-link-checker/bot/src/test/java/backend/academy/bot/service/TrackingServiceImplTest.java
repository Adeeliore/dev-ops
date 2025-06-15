package backend.academy.bot.service;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.dto.request.AddLinkRequest;
import backend.academy.bot.dto.request.RemoveLinkRequest;
import backend.academy.bot.dto.response.LinkResponse;
import backend.academy.bot.dto.response.ListLinksResponse;
import backend.academy.bot.service.impl.TrackingServiceImpl;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class TrackingServiceImplTest {

    @Mock
    private ScrapperClient scrapperClient;

    @InjectMocks
    private TrackingServiceImpl trackingService;

    private static final long CHAT_ID = 12345L;

    @BeforeEach
    void setUp() {
        reset(scrapperClient);
    }

    @Test
    void registerChat_Successful_ReturnsEmptyMono() {
        when(scrapperClient.registerChat(CHAT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(trackingService.registerChat(CHAT_ID)).verifyComplete();
        verify(scrapperClient).registerChat(CHAT_ID);
    }

    @Test
    void deleteChat_Successful_ReturnsEmptyMono() {
        when(scrapperClient.deleteChat(CHAT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(trackingService.deleteChat(CHAT_ID)).verifyComplete();
        verify(scrapperClient).deleteChat(CHAT_ID);
    }

    @Test
    void trackLink_Successful_ReturnsLinkResponse() {
        String url = "http://example.com";
        Set<String> tags = Set.of("tag1");
        Set<String> filters = Set.of("filter1");
        LinkResponse response = new LinkResponse(1L, url, tags, filters);
        AddLinkRequest request = new AddLinkRequest(url, tags, filters);
        when(scrapperClient.addLink(CHAT_ID, request)).thenReturn(Mono.just(response));

        StepVerifier.create(trackingService.trackLink(CHAT_ID, url, tags, filters))
                .expectNext(response)
                .verifyComplete();
        verify(scrapperClient).addLink(CHAT_ID, request);
    }

    @Test
    void untrackLink_Successful_ReturnsTrue() {
        String url = "http://example.com";
        RemoveLinkRequest request = new RemoveLinkRequest(url);
        LinkResponse response = new LinkResponse(1L, url, Collections.emptySet(), Collections.emptySet());
        when(scrapperClient.removeLink(CHAT_ID, request)).thenReturn(Mono.just(response));

        StepVerifier.create(trackingService.untrackLink(CHAT_ID, url))
                .expectNext(true)
                .verifyComplete();
        verify(scrapperClient).removeLink(CHAT_ID, request);
    }

    @Test
    void untrackLink_Error_ReturnsFalse() {
        String url = "http://example.com";
        RemoveLinkRequest request = new RemoveLinkRequest(url);
        when(scrapperClient.removeLink(CHAT_ID, request)).thenReturn(Mono.error(new RuntimeException("Error")));

        StepVerifier.create(trackingService.untrackLink(CHAT_ID, url))
                .expectNext(false)
                .verifyComplete();
        verify(scrapperClient).removeLink(CHAT_ID, request);
    }

    @Test
    void listTrackedLinks_Successful_ReturnsListLinksResponse() {
        ListLinksResponse response = new ListLinksResponse(
                Collections.singletonList(
                        new LinkResponse(1L, "http://example.com", Set.of("tag1"), Set.of("filter1"))),
                1);
        when(scrapperClient.getTrackedLinks(CHAT_ID)).thenReturn(Mono.just(response));

        StepVerifier.create(trackingService.listTrackedLinks(CHAT_ID))
                .expectNext(response)
                .verifyComplete();
        verify(scrapperClient).getTrackedLinks(CHAT_ID);
    }
}
