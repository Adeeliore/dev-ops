package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.model.ChatLink;
import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import java.util.List;

public class OrmChatLinkRepository implements ChatLinkRepository {
    private final JpaChatLinkRepository jpaRepository;

    public OrmChatLinkRepository(JpaChatLinkRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ChatLink chatLink) {
        jpaRepository.save(chatLink);
    }

    @Override
    public void deleteByChatIdAndLinkId(Long chatId, Long linkId) {
        jpaRepository.deleteByIdChatIdAndIdLinkId(chatId, linkId);
    }

    @Override
    public List<ChatLink> findByChatId(Long chatId) {
        return jpaRepository.findByIdChatId(chatId);
    }

    @Override
    public List<Long> findChatIdsByLinkId(Long linkId) {
        return jpaRepository.findByIdLinkId(linkId).stream()
                .map(cl -> cl.id().chatId())
                .toList();
    }

    @Override
    public List<Long> findChatIdsByLinkIdAndFilter(Long linkId, String filter) {
        return jpaRepository.findChatIdsByLinkIdAndFilter(linkId, filter);
    }

    @Override
    public List<Long> findChatIdsByLinkIdWithNoFilters(Long linkId) {
        return jpaRepository.findChatIdsByLinkIdWithNoFilters(linkId);
    }
}
