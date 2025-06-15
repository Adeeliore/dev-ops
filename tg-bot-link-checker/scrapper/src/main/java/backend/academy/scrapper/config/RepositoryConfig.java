package backend.academy.scrapper.config;

import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import backend.academy.scrapper.repository.interfaces.ChatRepository;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.interfaces.TagRepository;
import backend.academy.scrapper.repository.orm.JpaChatLinkRepository;
import backend.academy.scrapper.repository.orm.JpaChatRepository;
import backend.academy.scrapper.repository.orm.JpaLinkRepository;
import backend.academy.scrapper.repository.orm.JpaTagRepository;
import backend.academy.scrapper.repository.orm.OrmChatLinkRepository;
import backend.academy.scrapper.repository.orm.OrmChatRepository;
import backend.academy.scrapper.repository.orm.OrmLinkRepository;
import backend.academy.scrapper.repository.orm.OrmTagRepository;
import backend.academy.scrapper.repository.sql.SqlChatLinkRepository;
import backend.academy.scrapper.repository.sql.SqlChatRepository;
import backend.academy.scrapper.repository.sql.SqlLinkRepository;
import backend.academy.scrapper.repository.sql.SqlTagRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RepositoryConfig {

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "ORM")
    public ChatRepository ormChatRepository(JpaChatRepository jpaRepository) {
        return new OrmChatRepository(jpaRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "SQL")
    public ChatRepository sqlChatRepository(JdbcTemplate jdbcTemplate) {
        return new SqlChatRepository(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "ORM")
    public LinkRepository ormLinkRepository(JpaLinkRepository jpaRepository) {
        return new OrmLinkRepository(jpaRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "SQL")
    public LinkRepository sqlLinkRepository(JdbcTemplate jdbcTemplate) {
        return new SqlLinkRepository(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "ORM")
    public ChatLinkRepository ormChatLinkRepository(JpaChatLinkRepository jpaRepository) {
        return new OrmChatLinkRepository(jpaRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "SQL")
    public ChatLinkRepository sqlChatLinkRepository(JdbcTemplate jdbcTemplate) {
        return new SqlChatLinkRepository(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "ORM")
    public TagRepository ormTagRepository(JpaTagRepository jpaRepository) {
        return new OrmTagRepository(jpaRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "access-type", havingValue = "SQL")
    public TagRepository sqlTagRepository(JdbcTemplate jdbcTemplate) {
        return new SqlTagRepository(jdbcTemplate);
    }
}
