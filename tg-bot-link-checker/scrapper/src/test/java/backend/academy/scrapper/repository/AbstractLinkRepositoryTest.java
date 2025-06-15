package backend.academy.scrapper.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractLinkRepositoryTest extends BaseTest {

    @Autowired
    protected LinkRepository linkRepository;

    @Test
    @DisplayName("Сохранение новой ссылки")
    void shouldSaveLink() {
        Link link = new Link(null, "https://github.com/test/repo", LinkType.GITHUB, Instant.now());

        link = linkRepository.save(link);

        Link savedLink = linkRepository.findByUrl("https://github.com/test/repo");
        assertNotNull(savedLink);
        assertEquals("https://github.com/test/repo", savedLink.url());
        assertEquals(LinkType.GITHUB, savedLink.type());
    }

    @Test
    @DisplayName("Поиск ссылки по URL")
    void shouldFindLinkByUrl() {
        Link link = new Link(null, "https://github.com/test/repo2", LinkType.GITHUB, null);
        linkRepository.save(link);

        Link foundLink = linkRepository.findByUrl("https://github.com/test/repo2");
        assertNotNull(foundLink);
        assertEquals("https://github.com/test/repo2", foundLink.url());
        assertEquals(LinkType.GITHUB, foundLink.type());
    }

    @Test
    @DisplayName("Поиск всех ссылок по типу с пагинацией")
    void shouldFindAllLinksByType() {
        Link link1 = new Link(null, "https://github.com/test/repo3", LinkType.GITHUB, null);
        Link link2 = new Link(null, "https://github.com/test/repo4", LinkType.GITHUB, null);
        Link link3 = new Link(null, "https://stackoverflow.com/q/123", LinkType.STACKOVERFLOW, null);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        List<Link> found = linkRepository.findAllByType(LinkType.GITHUB, 0, 2);
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(link -> link.type() == LinkType.GITHUB));

        found = linkRepository.findAllByType(LinkType.GITHUB, 0, 1);
        assertEquals(1, found.size());
        assertEquals(LinkType.GITHUB, found.get(0).type());
    }

    @Test
    @DisplayName("Обновление времени последней проверки")
    void shouldUpdateLastChecked() {
        Link link = new Link(null, "https://github.com/test/repo5", LinkType.GITHUB, null);
        link = linkRepository.save(link);

        Instant newLastChecked = Instant.now();
        linkRepository.updateLastChecked(link.linkId(), newLastChecked);

        Link updatedLink = linkRepository.findByUrl("https://github.com/test/repo5");
        assertNotNull(updatedLink);
        assertEquals(
                newLastChecked.truncatedTo(ChronoUnit.MILLIS),
                updatedLink.lastChecked().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    @DisplayName("Подсчёт ссылок по типу")
    void shouldCountByType() {
        Link link1 = new Link(null, "https://github.com/test/repo6", LinkType.GITHUB, null);
        Link link2 = new Link(null, "https://github.com/test/repo7", LinkType.GITHUB, null);
        Link link3 = new Link(null, "https://stackoverflow.com/q/456", LinkType.STACKOVERFLOW, null);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        int count = linkRepository.countByType(LinkType.GITHUB);
        assertEquals(2, count);

        count = linkRepository.countByType(LinkType.STACKOVERFLOW);
        assertEquals(1, count);
    }
}
