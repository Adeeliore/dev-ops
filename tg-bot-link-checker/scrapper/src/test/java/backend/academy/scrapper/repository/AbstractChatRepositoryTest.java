package backend.academy.scrapper.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.repository.interfaces.ChatRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
public abstract class AbstractChatRepositoryTest extends BaseTest {

    @Autowired
    protected ChatRepository chatRepository;

    @Test
    @DisplayName("Сохранение нового чата")
    void shouldSaveChat() {
        Chat chat = new Chat();
        chat.chatId(1L);

        chatRepository.save(chat);

        Chat savedChat = chatRepository.findById(1L);
        assertNotNull(savedChat);
        assertEquals(1L, savedChat.chatId());
    }

    @Test
    @DisplayName("Удаление чата по ID")
    void shouldDeleteChatById() {
        Chat chat = new Chat();
        chat.chatId(2L);
        chatRepository.save(chat);

        chatRepository.deleteById(2L);

        assertNull(chatRepository.findById(2L));
    }

    @Test
    @DisplayName("Поиск чата по ID")
    void shouldFindChatById() {
        Chat chat = new Chat();
        chat.chatId(3L);
        chatRepository.save(chat);

        Chat foundChat = chatRepository.findById(3L);
        assertNotNull(foundChat);
        assertEquals(3L, foundChat.chatId());
    }
}
