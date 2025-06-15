package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.model.Tag;
import backend.academy.scrapper.repository.interfaces.TagRepository;

public class OrmTagRepository implements TagRepository {
    private final JpaTagRepository jpaRepository;

    public OrmTagRepository(JpaTagRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tag save(Tag tag) {
        return jpaRepository.save(tag);
    }

    @Override
    public Tag findByName(String name) {
        return jpaRepository.findByName(name).orElse(null);
    }
}
