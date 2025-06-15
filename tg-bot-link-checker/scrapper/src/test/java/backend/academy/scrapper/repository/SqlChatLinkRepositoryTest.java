package backend.academy.scrapper.repository;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "access-type=SQL")
class SqlChatLinkRepositoryTest extends AbstractChatLinkRepositoryTest {}
