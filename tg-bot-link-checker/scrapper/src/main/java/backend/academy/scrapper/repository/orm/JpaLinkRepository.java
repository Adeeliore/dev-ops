package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Link;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByUrl(String url);

    Page<Link> findByType(LinkType type, Pageable pageable);

    long countByType(LinkType type);
}
