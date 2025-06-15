package backend.academy.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.dto.response.ListLinksResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(TestcontainersConfiguration.class)
public class ScrapperClientCachingTest {

    @Autowired
    private ScrapperClient scrapperClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final WireMockServer wireMockServer = new WireMockServer(8081);

    @BeforeAll
    static void startMockServer() {
        wireMockServer.start();
        configureFor("localhost", 8081);
        stubFor(
                get(urlEqualTo("/api/v1/links"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                {
                  "links": [
                    {
                      "id": 1,
                      "url": "http://example.com",
                      "tags": ["tag1"],
                      "filters": ["filter1"]
                    }
                  ],
                  "size": 1
                }
                """)));
    }

    @AfterAll
    static void stopMockServer() {
        wireMockServer.stop();
    }

    @Test
    void shouldCacheTrackedLinks() {
        long chatId = 12345L;

        ListLinksResponse firstCall = scrapperClient.getTrackedLinks(chatId).block();

        ListLinksResponse secondCall = scrapperClient.getTrackedLinks(chatId).block();

        assertEquals(firstCall, secondCall);

        String redisKey = "tracked-links::" + chatId;
        assertTrue(redisTemplate.hasKey(redisKey));
    }
}
