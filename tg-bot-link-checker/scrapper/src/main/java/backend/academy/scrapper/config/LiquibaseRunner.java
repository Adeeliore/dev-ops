package backend.academy.scrapper.config;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiquibaseRunner {

    private final DataSource dataSource;

    @PostConstruct
    public void migrate() {
        try (Connection connection = dataSource.getConnection()) {
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase =
                    new Liquibase("migrations/changelog.yaml", new ClassLoaderResourceAccessor(), database);

            liquibase.update();
            log.info("✅ Миграции Liquibase успешно применены.");
        } catch (Exception e) {
            log.error("❌ Ошибка при применении миграций Liquibase.", e);
            throw new RuntimeException(e);
        }
    }
}
