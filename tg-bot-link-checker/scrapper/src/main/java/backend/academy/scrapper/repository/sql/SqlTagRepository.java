package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.model.Tag;
import backend.academy.scrapper.repository.interfaces.TagRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SqlTagRepository implements TagRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_OR_UPDATE_TAG = "INSERT INTO tags (name) " + "VALUES (?) "
            + "ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name "
            + "RETURNING tag_id";

    private static final String SELECT_TAG_BY_NAME = "SELECT * FROM tags WHERE name = ?";

    private final RowMapper<Tag> rowMapper = (rs, rowNum) -> {
        Tag tag = new Tag();
        tag.tagId(rs.getLong("tag_id"));
        tag.name(rs.getString("name"));
        return tag;
    };

    public SqlTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Tag save(Tag tag) {
        if (tag.tagId() == null) {
            Long id = jdbcTemplate.queryForObject(INSERT_OR_UPDATE_TAG, Long.class, tag.name());
            tag.tagId(id);
        }
        return tag;
    }

    @Override
    public Tag findByName(String name) {
        return jdbcTemplate.query(SELECT_TAG_BY_NAME, rowMapper, name).stream()
                .findFirst()
                .orElse(null);
    }
}
