package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.model.ChatLink;
import backend.academy.scrapper.model.ChatLinkId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaChatLinkRepository extends JpaRepository<ChatLink, ChatLinkId> {
    void deleteByIdChatIdAndIdLinkId(Long chatId, Long linkId);

    List<ChatLink> findByIdChatId(Long chatId);

    List<ChatLink> findByIdLinkId(Long linkId);

    @Query("SELECT cl.id.chatId FROM ChatLink cl JOIN cl.filters f WHERE cl.id.linkId = :linkId AND f = :filter")
    List<Long> findChatIdsByLinkIdAndFilter(@Param("linkId") Long linkId, @Param("filter") String filter);

    @Query("SELECT cl.id.chatId FROM ChatLink cl WHERE cl.id.linkId = :linkId AND cl.filters IS EMPTY")
    List<Long> findChatIdsByLinkIdWithNoFilters(@Param("linkId") Long linkId);
}
