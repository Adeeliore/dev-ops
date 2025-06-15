package backend.academy.scrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.exception.ChatNotRegisteredException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.model.ChatLink;
import backend.academy.scrapper.model.ChatLinkId;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.model.Tag;
import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import backend.academy.scrapper.repository.interfaces.ChatRepository;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.interfaces.TagRepository;
import backend.academy.scrapper.service.impl.TrackingServiceImpl;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "access-type=SQL")
@ExtendWith(MockitoExtension.class)
class TrackingServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ChatLinkRepository chatLinkRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private LinkMapper linkMapper;

    @InjectMocks
    private TrackingServiceImpl trackingService;

    @Test
    @DisplayName("Должен зарегистрировать чат")
    void shouldRegisterChat() {
        long chatId = 123L;
        trackingService.registerChat(chatId);
        verify(chatRepository).save(argThat(chat -> chat.chatId() == chatId));
    }

    @Test
    @DisplayName("Должен удалить чат")
    void shouldDeleteChat() {
        long chatId = 123L;
        trackingService.deleteChat(chatId);
        verify(chatRepository).deleteById(chatId);
    }

    @Test
    @DisplayName("Должен успешно добавить GitHub-репозиторий")
    void shouldAddGitHubRepoSuccessfully() {
        long chatId = 123L;
        String linkUrl = "https://github.com/user/repo";
        AddLinkRequest request = new AddLinkRequest(linkUrl, Set.of("tag1"), Set.of("filter1"));

        Chat chat = new Chat();
        chat.chatId(chatId);

        Link link = new Link();
        link.url(linkUrl);
        link.type(LinkType.GITHUB);

        Tag tag = new Tag();
        tag.name("tag1");

        when(chatRepository.findById(chatId)).thenReturn(chat);
        when(linkRepository.findByUrl(linkUrl)).thenReturn(null);
        doAnswer(invocation -> {
                    Link savedLink = invocation.getArgument(0);
                    savedLink.linkId(1L);
                    return savedLink;
                })
                .when(linkRepository)
                .save(any(Link.class));
        when(tagRepository.findByName("tag1")).thenReturn(null);
        doAnswer(invocation -> {
                    Tag savedTag = invocation.getArgument(0);
                    savedTag.tagId(1L);
                    return savedTag;
                })
                .when(tagRepository)
                .save(any(Tag.class));
        doNothing().when(chatLinkRepository).save(any(ChatLink.class));
        when(linkMapper.toLinkResponse(any(Link.class), any(Set.class), any(Set.class)))
                .thenReturn(new LinkResponse(1L, linkUrl, Set.of("tag1"), Set.of("filter1")));

        LinkResponse response = trackingService.addLink(chatId, request);

        assertNotNull(response);
        assertEquals(linkUrl, response.url());
        assertEquals(Set.of("tag1"), response.tags());
        assertEquals(Set.of("filter1"), response.filters());

        verify(chatRepository).findById(chatId);
        verify(linkRepository).findByUrl(linkUrl);
        verify(linkRepository).save(argThat(l -> l.url().equals(linkUrl) && l.type() == LinkType.GITHUB));
        verify(tagRepository).findByName("tag1");
        verify(tagRepository).save(argThat(t -> t.name().equals("tag1")));
        verify(chatLinkRepository)
                .save(argThat(cl -> cl.id().chatId() == chatId && cl.id().linkId() == 1L));
        verify(linkMapper).toLinkResponse(any(Link.class), any(Set.class), any(Set.class));
    }

    @Test
    @DisplayName("Должен выкинуть исключение при добавлении ссылки для несуществующего чата")
    void shouldThrowWhenAddingLinkForNonexistentChat() {
        long chatId = 123L;
        AddLinkRequest request = new AddLinkRequest("https://github.com/test/repo", Set.of(), Set.of());

        when(chatRepository.findById(chatId)).thenReturn(null);

        assertThrows(ChatNotRegisteredException.class, () -> trackingService.addLink(chatId, request));
    }

    @Test
    @DisplayName("Должен удалить ссылку")
    void shouldRemoveLink() {
        long chatId = 123L;
        String linkUrl = "https://github.com/test/repo";
        RemoveLinkRequest request = new RemoveLinkRequest(linkUrl);

        Link link = new Link();
        link.linkId(1L);
        link.url(linkUrl);

        when(linkRepository.findByUrl(linkUrl)).thenReturn(link);
        when(linkMapper.toLinkResponse(link, Set.of(), Set.of()))
                .thenReturn(new LinkResponse(1L, linkUrl, Set.of(), Set.of()));

        LinkResponse response = trackingService.removeLink(chatId, request);

        assertEquals(linkUrl, response.url());
        verify(chatLinkRepository).deleteByChatIdAndLinkId(chatId, link.linkId());
        verify(linkMapper).toLinkResponse(link, Set.of(), Set.of());
    }

    @Test
    @DisplayName("Должен выкинуть исключение при удалении несуществующей ссылки")
    void shouldThrowWhenRemovingNonexistentLink() {
        long chatId = 123L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://github.com/test/repo");

        when(linkRepository.findByUrl(request.link())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> trackingService.removeLink(chatId, request));
    }

    @Test
    @DisplayName("Должен получить отслеживаемые ссылки")
    void shouldGetTrackedLinks() {
        long chatId = 123L;

        Link link1 = new Link();
        link1.linkId(1L);
        link1.url("https://github.com/test/repo1");

        Link link2 = new Link();
        link2.linkId(2L);
        link2.url("https://github.com/test/repo2");

        Tag tag = new Tag();
        tag.tagId(1L);
        tag.name("tag1");

        ChatLink chatLink1 = new ChatLink();
        chatLink1.id(new ChatLinkId(chatId, link1.linkId()));
        chatLink1.chat(new Chat());
        chatLink1.chat().chatId(chatId);
        chatLink1.link(link1);
        chatLink1.tags(Set.of(tag));
        chatLink1.filters(Set.of("filter1"));

        ChatLink chatLink2 = new ChatLink();
        chatLink2.id(new ChatLinkId(chatId, link2.linkId()));
        chatLink2.chat(new Chat());
        chatLink2.chat().chatId(chatId);
        chatLink2.link(link2);
        chatLink2.tags(Set.of());
        chatLink2.filters(Set.of());

        List<ChatLink> chatLinks = List.of(chatLink1, chatLink2);

        when(chatLinkRepository.findByChatId(chatId)).thenReturn(chatLinks);
        when(linkMapper.toLinkResponse(link1, Set.of(tag), Set.of("filter1")))
                .thenReturn(new LinkResponse(1L, "https://github.com/test/repo1", Set.of("tag1"), Set.of("filter1")));
        when(linkMapper.toLinkResponse(link2, Set.of(), Set.of()))
                .thenReturn(new LinkResponse(2L, "https://github.com/test/repo2", Set.of(), Set.of()));

        ListLinksResponse response = trackingService.getTrackedLinks(chatId);

        assertEquals(2, response.size());
        assertEquals("https://github.com/test/repo1", response.links().get(0).url());
        assertEquals("https://github.com/test/repo2", response.links().get(1).url());

        verify(chatLinkRepository).findByChatId(chatId);
        verify(linkMapper).toLinkResponse(link1, Set.of(tag), Set.of("filter1"));
        verify(linkMapper).toLinkResponse(link2, Set.of(), Set.of());
    }
}
