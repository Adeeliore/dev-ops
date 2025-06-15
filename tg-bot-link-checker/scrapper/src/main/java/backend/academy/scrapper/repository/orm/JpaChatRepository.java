package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRepository extends JpaRepository<Chat, Long> {}
