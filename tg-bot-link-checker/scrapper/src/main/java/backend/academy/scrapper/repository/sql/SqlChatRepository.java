package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.repository.interfaces.ChatRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SqlChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_CHAT = "INSERT INTO chats (chat_id) VALUES (?) ON CONFLICT DO NOTHING";

    private static final String DELETE_CHAT_BY_ID = "DELETE FROM chats WHERE chat_id = ?";

    private static final String SELECT_CHAT_BY_ID = "SELECT * FROM chats WHERE chat_id = ?";

    private final RowMapper<Chat> rowMapper = (rs, rowNum) -> {
        Chat chat = new Chat();
        chat.chatId(rs.getLong("chat_id"));
        return chat;
    };

    public SqlChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Chat chat) {
        jdbcTemplate.update(INSERT_CHAT, chat.chatId());
    }

    @Override
    public void deleteById(Long chatId) {
        jdbcTemplate.update(DELETE_CHAT_BY_ID, chatId);
    }

    @Override
    public Chat findById(Long chatId) {
        return jdbcTemplate.query(SELECT_CHAT_BY_ID, rowMapper, chatId).stream()
                .findFirst()
                .orElse(null);
    }
}
