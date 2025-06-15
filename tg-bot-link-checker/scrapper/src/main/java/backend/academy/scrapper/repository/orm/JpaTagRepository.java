package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.model.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
