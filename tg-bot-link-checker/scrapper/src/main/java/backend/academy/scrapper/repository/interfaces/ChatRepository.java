package backend.academy.scrapper.repository.interfaces;

import backend.academy.scrapper.model.Chat;

public interface ChatRepository {
    void save(Chat chat);

    void deleteById(Long chatId);

    Chat findById(Long chatId);
}
