package backend.academy.scrapper.repository.interfaces;

import backend.academy.scrapper.model.Tag;

public interface TagRepository {
    Tag save(Tag tag);

    Tag findByName(String name);
}
