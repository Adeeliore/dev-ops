package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.model.ChatLink;
import backend.academy.scrapper.model.ChatLinkId;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.model.Tag;
import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@RequiredArgsConstructor
public class SqlChatLinkRepository implements ChatLinkRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_CHAT_LINK = "INSERT INTO chat_links (chat_id, link_id) VALUES (?, ?)";

    private static final String INSERT_TAGS = "INSERT INTO chat_link_tags (chat_id, link_id, tag_id) VALUES (?, ?, ?)";

    private static final String INSERT_FILTERS =
            "INSERT INTO chat_link_filters (chat_id, link_id, filter) VALUES (?, ?, ?)";

    private static final String DELETE_CHAT_LINK = "DELETE FROM chat_links WHERE chat_id = ? AND link_id = ?";

    private static final String SELECT_CHAT_LINKS =
            """
        SELECT cl.chat_id, cl.link_id,
               l.url, l.type, l.last_checked
          FROM chat_links cl
          JOIN links l ON l.link_id = cl.link_id
         WHERE cl.chat_id = ?
        """;

    private static final String SELECT_TAG_IDS = "SELECT tag_id FROM chat_link_tags WHERE chat_id = ? AND link_id = ?";

    private static final String SELECT_FILTERS =
            "SELECT filter FROM chat_link_filters WHERE chat_id = ? AND link_id = ?";

    private static final String SELECT_CHAT_IDS_BY_LINK = "SELECT chat_id FROM chat_links WHERE link_id = ?";

    private static final String SELECT_CHAT_IDS_BY_LINK_AND_FILTER =
            "SELECT DISTINCT chat_id FROM chat_link_filters WHERE link_id = ? AND filter = ?";

    private static final String SELECT_CHAT_IDS_BY_LINK_NO_FILTERS =
            """
        SELECT cl.chat_id
          FROM chat_links cl
         WHERE cl.link_id = ?
           AND NOT EXISTS (
               SELECT 1 FROM chat_link_filters f
                WHERE f.chat_id = cl.chat_id
                  AND f.link_id = cl.link_id
           )
        """;

    @Override
    @Transactional
    public void save(ChatLink chatLink) {
        Long chatId = chatLink.id().chatId();
        Long linkId = chatLink.id().linkId();

        jdbcTemplate.update(INSERT_CHAT_LINK, chatId, linkId);

        List<Object[]> tagParams = chatLink.tags().stream()
                .map(t -> new Object[] {chatId, linkId, t.tagId()})
                .toList();
        if (!tagParams.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT_TAGS, tagParams);
        }

        List<Object[]> filterParams = chatLink.filters().stream()
                .map(f -> new Object[] {chatId, linkId, f})
                .toList();
        if (!filterParams.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT_FILTERS, filterParams);
        }
    }

    @Override
    @Transactional
    public void deleteByChatIdAndLinkId(Long chatId, Long linkId) {
        jdbcTemplate.update(DELETE_CHAT_LINK, chatId, linkId);
    }

    @Override
    @Transactional
    public List<ChatLink> findByChatId(Long chatId) {
        List<ChatLink> base = jdbcTemplate.query(SELECT_CHAT_LINKS, chatLinkRowMapper(), chatId);

        base.forEach(cl -> {
            Long c = cl.id().chatId();
            Long l = cl.id().linkId();
            cl.tags(fetchTags(c, l));
            cl.filters(fetchFilters(c, l));
        });

        return base;
    }

    @Override
    @Transactional
    public List<Long> findChatIdsByLinkId(Long linkId) {
        return jdbcTemplate.query(SELECT_CHAT_IDS_BY_LINK, (rs, rowNum) -> rs.getLong("chat_id"), linkId);
    }

    @Override
    @Transactional
    public List<Long> findChatIdsByLinkIdAndFilter(Long linkId, String filter) {
        return jdbcTemplate.query(
                SELECT_CHAT_IDS_BY_LINK_AND_FILTER, (rs, rowNum) -> rs.getLong("chat_id"), linkId, filter);
    }

    @Override
    @Transactional
    public List<Long> findChatIdsByLinkIdWithNoFilters(Long linkId) {
        return jdbcTemplate.query(SELECT_CHAT_IDS_BY_LINK_NO_FILTERS, (rs, rowNum) -> rs.getLong("chat_id"), linkId);
    }

    private RowMapper<ChatLink> chatLinkRowMapper() {
        return (rs, rowNum) -> {
            ChatLink cl = new ChatLink();
            ChatLinkId id = new ChatLinkId();
            id.chatId(rs.getLong("chat_id"));
            id.linkId(rs.getLong("link_id"));
            cl.id(id);

            Chat chat = new Chat();
            chat.chatId(rs.getLong("chat_id"));
            cl.chat(chat);

            Link link = new Link();
            link.linkId(rs.getLong("link_id"));
            link.url(rs.getString("url"));
            link.type(LinkType.valueOf(rs.getString("type")));
            Timestamp ts = rs.getTimestamp("last_checked");
            link.lastChecked(ts != null ? ts.toInstant() : null);
            cl.link(link);

            cl.tags(new HashSet<>());
            cl.filters(new HashSet<>());

            return cl;
        };
    }

    private Set<Tag> fetchTags(Long chatId, Long linkId) {
        List<Long> tagIds = jdbcTemplate.query(SELECT_TAG_IDS, (rs, rn) -> rs.getLong("tag_id"), chatId, linkId);
        return tagIds.stream()
                .map(id -> {
                    Tag t = new Tag();
                    t.tagId(id);
                    return t;
                })
                .collect(Collectors.toSet());
    }

    private Set<String> fetchFilters(Long chatId, Long linkId) {
        return new HashSet<>(jdbcTemplate.query(SELECT_FILTERS, (rs, rn) -> rs.getString("filter"), chatId, linkId));
    }
}
