package backend.academy.scrapper.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import backend.academy.scrapper.model.Tag;
import backend.academy.scrapper.repository.interfaces.TagRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
public abstract class AbstractTagRepositoryTest extends BaseTest {

    @Autowired
    protected TagRepository tagRepository;

    @Test
    @DisplayName("Сохранение нового тега")
    void shouldSaveTag() {
        Tag tag = new Tag();
        tag.name("test-tag");

        tag = tagRepository.save(tag);

        Tag savedTag = tagRepository.findByName("test-tag");
        assertNotNull(savedTag);
        assertEquals("test-tag", savedTag.name());
    }

    @Test
    @DisplayName("Поиск тега по имени")
    void shouldFindTagByName() {
        Tag tag = new Tag();
        tag.name("test-tag2");
        tagRepository.save(tag);

        Tag foundTag = tagRepository.findByName("test-tag2");
        assertNotNull(foundTag);
        assertEquals("test-tag2", foundTag.name());
    }
}
