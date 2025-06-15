package backend.academy.scrapper.repository.interfaces;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Link;
import java.time.Instant;
import java.util.List;

public interface LinkRepository {
    Link save(Link link);

    Link findByUrl(String url);

    List<Link> findAllByType(LinkType type, int offset, int limit);

    void updateLastChecked(Long linkId, Instant lastChecked);

    int countByType(LinkType type);
}
