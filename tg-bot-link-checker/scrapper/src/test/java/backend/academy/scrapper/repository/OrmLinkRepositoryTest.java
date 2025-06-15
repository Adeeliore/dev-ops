package backend.academy.scrapper.repository;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "access-type=ORM")
class OrmLinkRepositoryTest extends AbstractLinkRepositoryTest {}
