package backend.academy.scrapper.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.constants.Constants;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.client.Issue;
import backend.academy.scrapper.dto.client.PullRequest;
import backend.academy.scrapper.dto.client.User;
import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.service.BotNotificationService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class LinkUpdateSchedulerTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ChatLinkRepository chatLinkRepository;

    @Mock
    private BotNotificationService notificationService;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    private LinkUpdateScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new LinkUpdateScheduler(
                linkRepository,
                chatLinkRepository,
                notificationService,
                gitHubClient,
                stackOverflowClient,
                taskExecutor);
        ReflectionTestUtils.setField(scheduler, "batchSize", 1000);
    }

    @Test
    @DisplayName("Планировщик должен корректно уведомлять о новых PR и Issues из GitHub")
    void shouldNotifyOnNewGitHubPullRequestsAndIssues() {
        Link link = new Link(
                123L,
                "https://github.com/test/repo",
                LinkType.GITHUB,
                Instant.now().minusSeconds(3600));

        PullRequest pr = new PullRequest("Fix bug", new User("alice"), Instant.now(), "Details...");
        Issue issue = new Issue("Crash", new User("Artemii"), Instant.now(), "Stacktrace...");

        when(linkRepository.countByType(LinkType.GITHUB)).thenReturn(1);
        when(linkRepository.findAllByType(LinkType.GITHUB, 0, 1000)).thenReturn(List.of(link));

        when(gitHubClient.fetchPullRequests("test", "repo", link.lastChecked())).thenReturn(Mono.just(List.of(pr)));
        when(gitHubClient.fetchIssues("test", "repo", link.lastChecked())).thenReturn(Mono.just(List.of(issue)));

        when(chatLinkRepository.findChatIdsByLinkIdAndFilter(123L, Constants.FILTER_PR))
                .thenReturn(List.of(1L));
        when(chatLinkRepository.findChatIdsByLinkIdAndFilter(123L, Constants.FILTER_ISSUE))
                .thenReturn(List.of(1L));
        when(chatLinkRepository.findChatIdsByLinkIdAndFilter(123L, "user=alice"))
                .thenReturn(List.of(2L));
        when(chatLinkRepository.findChatIdsByLinkIdAndFilter(123L, "user=Artemii"))
                .thenReturn(List.of());
        when(chatLinkRepository.findChatIdsByLinkIdWithNoFilters(123L)).thenReturn(List.of(3L));

        doAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    runnable.run();
                    return null;
                })
                .when(taskExecutor)
                .execute(any(Runnable.class));

        scheduler.checkUpdates();

        ArgumentCaptor<LinkUpdate> updateCaptor = ArgumentCaptor.forClass(LinkUpdate.class);
        verify(notificationService, times(2)).notifyUpdate(updateCaptor.capture());

        List<LinkUpdate> updates = updateCaptor.getAllValues();

        LinkUpdate prUpdate = updates.get(0);
        LinkUpdate issueUpdate = updates.get(1);

        assertEquals(123L, prUpdate.id());
        assertTrue(prUpdate.description().contains("Fix bug"));
        assertEquals(List.of(1L, 3L), prUpdate.tgChatIds());

        assertEquals(123L, issueUpdate.id());
        assertTrue(issueUpdate.description().contains("Crash"));
        assertEquals(List.of(1L, 3L), issueUpdate.tgChatIds());
    }
}
