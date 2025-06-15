package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.constants.Constants;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.dto.client.Answer;
import backend.academy.scrapper.dto.client.Comment;
import backend.academy.scrapper.dto.client.Issue;
import backend.academy.scrapper.dto.client.PullRequest;
import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.service.BotNotificationService;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkUpdateScheduler {
    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final BotNotificationService notificationService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Value("${scheduler.batch-size:1000}")
    private int batchSize;

    @Scheduled(fixedRateString = "${scheduler.interval}", timeUnit = TimeUnit.MINUTES)
    public void checkUpdates() {
        log.info("Checking updates...");
        for (LinkType type : LinkType.values()) {
            int total = linkRepository.countByType(type);
            for (int offset = 0; offset < total; offset += batchSize) {
                List<Link> links = linkRepository.findAllByType(type, offset, batchSize);
                for (Link link : links) {
                    log.info("Processing link: {}", link.url());
                    taskExecutor.execute(() -> processLink(link));
                }
            }
        }
    }

    private void processLink(Link link) {
        Instant now = Instant.now();
        if (link.type() == LinkType.GITHUB) {
            processGitHubLink(link);
        } else if (link.type() == LinkType.STACKOVERFLOW) {
            processStackOverflowLink(link);
        }
        linkRepository.updateLastChecked(link.linkId(), now);
    }

    private void processGitHubLink(Link link) {
        URI uri = URI.create(link.url());
        String[] parts = uri.getPath().split("/");
        String owner = parts[1];
        String repo = parts[2];

        List<PullRequest> prs =
                gitHubClient.fetchPullRequests(owner, repo, link.lastChecked()).block();
        List<Issue> issues =
                gitHubClient.fetchIssues(owner, repo, link.lastChecked()).block();

        if (prs != null) {
            for (PullRequest pr : prs) {
                List<Long> chatsToNotify =
                        getChatsToNotify(link, Constants.FILTER_PR, pr.user().login());
                if (!chatsToNotify.isEmpty()) {
                    String msg = pr.generateMessage(repo);
                    notificationService.notifyUpdate(new LinkUpdate(link.linkId(), link.url(), msg, chatsToNotify));
                }
            }
        }

        if (issues != null) {
            for (Issue issue : issues) {
                List<Long> chatsToNotify = getChatsToNotify(
                        link, Constants.FILTER_ISSUE, issue.user().login());
                if (!chatsToNotify.isEmpty()) {
                    String msg = issue.generateMessage(repo);
                    notificationService.notifyUpdate(new LinkUpdate(link.linkId(), link.url(), msg, chatsToNotify));
                }
            }
        }
    }

    private void processStackOverflowLink(Link link) {
        URI uri = URI.create(link.url());
        long questionId = Long.parseLong(uri.getPath().split("/")[2]);
        List<Answer> answers =
                stackOverflowClient.fetchAnswers(questionId, link.lastChecked()).block();
        List<Comment> comments = stackOverflowClient
                .fetchComments(questionId, link.lastChecked())
                .block();

        if (answers != null) {
            for (Answer answer : answers) {
                List<Long> chatsToNotify = getChatsToNotify(link, Constants.FILTER_ANSWER, answer.user());
                if (!chatsToNotify.isEmpty()) {
                    String msg = answer.generateMessage();
                    notificationService.notifyUpdate(new LinkUpdate(link.linkId(), link.url(), msg, chatsToNotify));
                }
            }
        }

        if (comments != null) {
            for (Comment comment : comments) {
                List<Long> chatsToNotify = getChatsToNotify(link, Constants.FILTER_COMMENT, comment.user());
                if (!chatsToNotify.isEmpty()) {
                    String msg = comment.generateMessage();
                    notificationService.notifyUpdate(new LinkUpdate(link.linkId(), link.url(), msg, chatsToNotify));
                }
            }
        }
    }

    private List<Long> getChatsToNotify(Link link, String eventFilter, String author) {
        List<Long> allChatsForLink = chatLinkRepository.findChatIdsByLinkId(link.linkId());

        Set<Long> chatsToNotify = new HashSet<>();
        if (eventFilter != null) {
            List<Long> chatsWithEventFilter =
                    chatLinkRepository.findChatIdsByLinkIdAndFilter(link.linkId(), eventFilter);
            List<Long> chatsWithNoFilters = chatLinkRepository.findChatIdsByLinkIdWithNoFilters(link.linkId());
            chatsToNotify.addAll(chatsWithEventFilter);
            chatsToNotify.addAll(chatsWithNoFilters);
        } else {
            chatsToNotify.addAll(allChatsForLink);
        }

        if (author != null && !author.isEmpty()) {
            String userFilter = "user=" + author;
            Set<Long> chatsWithUserFilter =
                    new HashSet<>(chatLinkRepository.findChatIdsByLinkIdAndFilter(link.linkId(), userFilter));
            chatsToNotify.removeAll(chatsWithUserFilter);
        }

        return new ArrayList<>(chatsToNotify);
    }
}
