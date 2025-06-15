package backend.academy.scrapper.repository.interfaces;

import backend.academy.scrapper.model.ChatLink;
import java.util.List;

public interface ChatLinkRepository {
    void save(ChatLink chatLink);

    void deleteByChatIdAndLinkId(Long chatId, Long linkId);

    List<ChatLink> findByChatId(Long chatId);

    List<Long> findChatIdsByLinkId(Long linkId);

    List<Long> findChatIdsByLinkIdAndFilter(Long linkId, String filter);

    List<Long> findChatIdsByLinkIdWithNoFilters(Long linkId);
}
