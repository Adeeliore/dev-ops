package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.repository.interfaces.ChatRepository;

public class OrmChatRepository implements ChatRepository {
    private final JpaChatRepository jpaRepository;

    public OrmChatRepository(JpaChatRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Chat chat) {
        jpaRepository.save(chat);
    }

    @Override
    public void deleteById(Long chatId) {
        jpaRepository.deleteById(chatId);
    }

    @Override
    public Chat findById(Long chatId) {
        return jpaRepository.findById(chatId).orElse(null);
    }
}
