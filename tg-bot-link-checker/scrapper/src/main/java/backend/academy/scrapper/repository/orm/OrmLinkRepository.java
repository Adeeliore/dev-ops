package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.PageRequest;

public class OrmLinkRepository implements LinkRepository {
    private final JpaLinkRepository jpaRepository;

    public OrmLinkRepository(JpaLinkRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Link save(Link link) {
        return jpaRepository.save(link);
    }

    @Override
    public Link findByUrl(String url) {
        return jpaRepository.findByUrl(url).orElse(null);
    }

    @Override
    public List<Link> findAllByType(LinkType type, int offset, int limit) {
        return jpaRepository
                .findByType(type, PageRequest.of(offset / limit, limit))
                .getContent();
    }

    @Override
    public void updateLastChecked(Long linkId, Instant lastChecked) {
        Link link = jpaRepository.findById(linkId).orElseThrow();
        link.lastChecked(lastChecked);
        jpaRepository.save(link);
    }

    @Override
    public int countByType(LinkType type) {
        return (int) jpaRepository.countByType(type);
    }
}
