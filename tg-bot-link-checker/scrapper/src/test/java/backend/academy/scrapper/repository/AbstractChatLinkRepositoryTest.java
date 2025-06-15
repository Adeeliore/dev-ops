package backend.academy.scrapper.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.model.ChatLink;
import backend.academy.scrapper.model.ChatLinkId;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.model.Tag;
import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import backend.academy.scrapper.repository.interfaces.ChatRepository;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.interfaces.TagRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Transactional
public abstract class AbstractChatLinkRepositoryTest extends BaseTest {

    @Autowired
    protected ChatLinkRepository chatLinkRepository;

    @Autowired
    protected ChatRepository chatRepository;

    @Autowired
    protected LinkRepository linkRepository;

    @Autowired
    protected TagRepository tagRepository;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Сохранение связи чат-ссылка")
    void shouldSaveChatLink() {
        Chat chat = new Chat();
        chat.chatId(1L);
        chatRepository.save(chat);

        Link link = new Link(null, "https://github.com/test/repo", LinkType.GITHUB, null);
        link = linkRepository.save(link);

        Tag tag = new Tag();
        tag.name("test-tag");
        tagRepository.save(tag);

        ChatLinkId id = new ChatLinkId(chat.chatId(), link.linkId());
        ChatLink chatLink = new ChatLink();
        chatLink.id(id);
        chatLink.chat(chat);
        chatLink.link(link);
        chatLink.filters(Set.of("filter1", "filter2"));
        chatLink.tags(Set.of(tag));
        chatLinkRepository.save(chatLink);

        List<ChatLink> found = chatLinkRepository.findByChatId(chat.chatId());
        assertEquals(1, found.size());
        assertEquals(chat.chatId(), found.get(0).id().chatId());
        assertEquals(link.linkId(), found.get(0).id().linkId());
        assertEquals(Set.of("filter1", "filter2"), found.get(0).filters());
        assertEquals(1, found.get(0).tags().size());
    }

    @Test
    @DisplayName("Удаление связи чат-ссылка по ID")
    void shouldDeleteChatLinkByChatIdAndLinkId() {
        Chat chat = new Chat();
        chat.chatId(2L);
        Link link = new Link(null, "https://github.com/test/repo2", LinkType.GITHUB, null);
        chatRepository.save(chat);
        link = linkRepository.save(link);

        ChatLinkId id = new ChatLinkId(2L, link.linkId());
        ChatLink chatLink = new ChatLink();
        chatLink.id(id);
        chatLink.chat(chat);
        chatLink.link(link);
        chatLinkRepository.save(chatLink);

        chatLinkRepository.deleteByChatIdAndLinkId(2L, link.linkId());

        List<ChatLink> found = chatLinkRepository.findByChatId(2L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск связей по ID чата")
    void shouldFindChatLinksByChatId() {
        Chat chat = new Chat();
        chat.chatId(3L);
        Link link1 = new Link(null, "https://github.com/test/repo3", LinkType.GITHUB, null);
        Link link2 = new Link(null, "https://github.com/test/repo4", LinkType.GITHUB, null);
        chatRepository.save(chat);
        link1 = linkRepository.save(link1);
        link2 = linkRepository.save(link2);

        ChatLinkId id1 = new ChatLinkId(3L, link1.linkId());
        ChatLinkId id2 = new ChatLinkId(3L, link2.linkId());
        ChatLink chatLink1 = new ChatLink();
        chatLink1.id(id1);
        chatLink1.chat(chat);
        chatLink1.link(link1);
        chatLink1.filters(Set.of("filter1"));
        ChatLink chatLink2 = new ChatLink();
        chatLink2.id(id2);
        chatLink2.chat(chat);
        chatLink2.link(link2);

        chatLinkRepository.save(chatLink1);
        chatLinkRepository.save(chatLink2);

        List<ChatLink> found = chatLinkRepository.findByChatId(3L);
        assertEquals(2, found.size());

        Long linkId1 = link1.linkId();
        Long linkId2 = link2.linkId();
        assertTrue(found.stream().anyMatch(cl -> cl.id().linkId().equals(linkId1)));
        assertTrue(found.stream().anyMatch(cl -> cl.id().linkId().equals(linkId2)));
    }

    @Test
    @DisplayName("Поиск ID чатов по ID ссылки")
    void shouldFindChatIdsByLinkId() {
        Chat chat1 = new Chat();
        chat1.chatId(4L);
        Chat chat2 = new Chat();
        chat2.chatId(5L);
        Link link = new Link(null, "https://github.com/test/repo5", LinkType.GITHUB, null);
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        link = linkRepository.save(link);

        ChatLinkId id1 = new ChatLinkId(4L, link.linkId());
        ChatLinkId id2 = new ChatLinkId(5L, link.linkId());
        ChatLink chatLink1 = new ChatLink();
        chatLink1.id(id1);
        chatLink1.chat(chat1);
        chatLink1.link(link);
        ChatLink chatLink2 = new ChatLink();
        chatLink2.id(id2);
        chatLink2.chat(chat2);
        chatLink2.link(link);

        chatLinkRepository.save(chatLink1);
        chatLinkRepository.save(chatLink2);

        List<Long> chatIds = chatLinkRepository.findChatIdsByLinkId(link.linkId());
        assertEquals(2, chatIds.size());
        assertTrue(chatIds.contains(4L));
        assertTrue(chatIds.contains(5L));
    }

    @Test
    @DisplayName("Поиск ID чатов по ID ссылки и фильтру")
    void shouldFindChatIdsByLinkIdAndFilter() {
        Chat chat1 = new Chat();
        chat1.chatId(6L);
        Chat chat2 = new Chat();
        chat2.chatId(7L);
        Link link = new Link(null, "https://github.com/test/repo6", LinkType.GITHUB, null);
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        link = linkRepository.save(link);

        ChatLinkId id1 = new ChatLinkId(6L, link.linkId());
        ChatLinkId id2 = new ChatLinkId(7L, link.linkId());
        ChatLink chatLink1 = new ChatLink();
        chatLink1.id(id1);
        chatLink1.chat(chat1);
        chatLink1.link(link);
        chatLink1.filters(Set.of("filter1"));
        ChatLink chatLink2 = new ChatLink();
        chatLink2.id(id2);
        chatLink2.chat(chat2);
        chatLink2.link(link);
        chatLink2.filters(Set.of("filter1", "filter2"));

        chatLinkRepository.save(chatLink1);
        chatLinkRepository.save(chatLink2);

        List<Long> chatIds = chatLinkRepository.findChatIdsByLinkIdAndFilter(link.linkId(), "filter1");
        assertEquals(2, chatIds.size());
        assertTrue(chatIds.contains(6L));
        assertTrue(chatIds.contains(7L));
    }

    @Test
    @DisplayName("Поиск ID чатов по ID ссылки без фильтров")
    void shouldFindChatIdsByLinkIdWithNoFilters() {
        Chat chat1 = new Chat();
        chat1.chatId(8L);
        Chat chat2 = new Chat();
        chat2.chatId(9L);
        Link link = new Link(null, "https://github.com/test/repo7", LinkType.GITHUB, null);
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        link = linkRepository.save(link);

        ChatLinkId id1 = new ChatLinkId(8L, link.linkId());
        ChatLinkId id2 = new ChatLinkId(9L, link.linkId());
        ChatLink chatLink1 = new ChatLink();
        chatLink1.id(id1);
        chatLink1.chat(chat1);
        chatLink1.link(link);
        ChatLink chatLink2 = new ChatLink();
        chatLink2.id(id2);
        chatLink2.chat(chat2);
        chatLink2.link(link);
        chatLink2.filters(Set.of("filter1"));

        chatLinkRepository.save(chatLink1);
        chatLinkRepository.save(chatLink2);

        List<Long> chatIds = chatLinkRepository.findChatIdsByLinkIdWithNoFilters(link.linkId());
        assertEquals(1, chatIds.size());
        assertTrue(chatIds.contains(8L));
    }
}
