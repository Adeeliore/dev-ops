package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SqlLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_LINK =
            "INSERT INTO links (url, type, last_checked) VALUES (?, ?::link_type, ?) RETURNING link_id";

    private static final String UPDATE_LINK =
            "UPDATE links SET url = ?, type = ?::link_type, last_checked = ? WHERE link_id = ?";

    private static final String SELECT_LINK_BY_URL = "SELECT * FROM links WHERE url = ?";

    private static final String SELECT_LINKS_BY_TYPE =
            "SELECT * FROM links WHERE type = ?::link_type ORDER BY link_id LIMIT ? OFFSET ?";

    private static final String UPDATE_LAST_CHECKED = "UPDATE links SET last_checked = ? WHERE link_id = ?";

    private static final String COUNT_LINKS_BY_TYPE = "SELECT COUNT(*) FROM links WHERE type = ?::link_type";

    private final RowMapper<Link> rowMapper = (rs, rowNum) -> {
        Link link = new Link();
        link.linkId(rs.getLong("link_id"));
        link.url(rs.getString("url"));
        link.type(LinkType.valueOf(rs.getString("type")));

        Timestamp ts = rs.getTimestamp("last_checked");
        link.lastChecked(ts != null ? ts.toInstant() : null);

        return link;
    };

    public SqlLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Link save(Link link) {
        if (link.linkId() == null) {
            Long id = jdbcTemplate.queryForObject(
                    INSERT_LINK,
                    Long.class,
                    link.url(),
                    link.type().name(),
                    link.lastChecked() != null ? Timestamp.from(link.lastChecked()) : null);
            link.linkId(id);
        } else {
            jdbcTemplate.update(
                    UPDATE_LINK,
                    link.url(),
                    link.type().name(),
                    link.lastChecked() != null ? Timestamp.from(link.lastChecked()) : null,
                    link.linkId());
        }
        return link;
    }

    @Override
    public Link findByUrl(String url) {
        return jdbcTemplate.query(SELECT_LINK_BY_URL, rowMapper, url).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Link> findAllByType(LinkType type, int offset, int limit) {
        return jdbcTemplate.query(SELECT_LINKS_BY_TYPE, rowMapper, type.name(), limit, offset);
    }

    @Override
    public void updateLastChecked(Long linkId, Instant lastChecked) {
        jdbcTemplate.update(UPDATE_LAST_CHECKED, Timestamp.from(lastChecked), linkId);
    }

    @Override
    public int countByType(LinkType type) {
        Integer count = jdbcTemplate.queryForObject(COUNT_LINKS_BY_TYPE, Integer.class, type.name());
        return count != null ? count : 0;
    }
}
