package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import backend.academy.scrapper.model.TrackedGitHubRepo;
import backend.academy.scrapper.model.TrackedStackOverflowQuestion;
import backend.academy.scrapper.repository.LinkRepository;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class InMemoryLinkRepository implements LinkRepository {
    private final Map<Long, Set<TrackedGitHubRepo>> trackedGitHubRepos = new ConcurrentHashMap<>();
    private final Map<Long, Set<TrackedStackOverflowQuestion>> trackedStackOverflowQuestions =
            new ConcurrentHashMap<>();

    @Override
    public void initChat(long chatId) {
        trackedGitHubRepos.putIfAbsent(chatId, ConcurrentHashMap.newKeySet());
        trackedStackOverflowQuestions.putIfAbsent(chatId, ConcurrentHashMap.newKeySet());
    }

    @Override
    public boolean chatExists(long chatId) {
        return trackedGitHubRepos.containsKey(chatId) || trackedStackOverflowQuestions.containsKey(chatId);
    }

    @Override
    public boolean isLinkTracked(long chatId, String link) {
        return trackedGitHubRepos.getOrDefault(chatId, Collections.emptySet()).stream()
                        .anyMatch(repo -> repo.url().equals(link))
                || trackedStackOverflowQuestions.getOrDefault(chatId, Collections.emptySet()).stream()
                        .anyMatch(q -> q.url().equals(link));
    }

    @Override
    public void clearChat(long chatId) {
        trackedGitHubRepos.remove(chatId);
        trackedStackOverflowQuestions.remove(chatId);
    }

    @Override
    public void addGitHubRepo(long chatId, GitHubRepoResponse response, Set<String> tags, Set<String> filters) {
        trackedGitHubRepos
                .get(chatId)
                .add(new TrackedGitHubRepo(
                        chatId,
                        response.url(),
                        Instant.parse(response.updatedAt()),
                        response.openIssues(),
                        response.stars(),
                        response.forks(),
                        tags,
                        filters));
    }

    @Override
    public void addStackOverflowQuestion(
            long chatId, StackOverflowQuestionResponse.QuestionItem item, Set<String> tags, Set<String> filters) {
        trackedStackOverflowQuestions
                .get(chatId)
                .add(new TrackedStackOverflowQuestion(
                        chatId,
                        item.link(),
                        Instant.ofEpochSecond(item.lastActivityDate()),
                        item.answerCount(),
                        item.score(),
                        item.viewCount(),
                        tags,
                        filters));
    }

    @Override
    public boolean removeLink(long chatId, String url) {
        boolean removed = trackedGitHubRepos.getOrDefault(chatId, Set.of()).removeIf(repo -> repo.url()
                .equals(url));
        removed |= trackedStackOverflowQuestions.getOrDefault(chatId, Set.of()).removeIf(q -> q.url()
                .equals(url));
        return removed;
    }

    @Override
    public List<TrackedGitHubRepo> getGitHubLinksByChatId(long chatId) {
        return new ArrayList<>(trackedGitHubRepos.getOrDefault(chatId, Set.of()));
    }

    @Override
    public List<TrackedStackOverflowQuestion> getStackOverflowLinksByChatId(long chatId) {
        return new ArrayList<>(trackedStackOverflowQuestions.getOrDefault(chatId, Set.of()));
    }

    @Override
    public List<TrackedGitHubRepo> getAllGitHubLinks() {
        return trackedGitHubRepos.values().stream().flatMap(Set::stream).toList();
    }

    @Override
    public List<TrackedStackOverflowQuestion> getAllStackOverflowLinks() {
        return trackedStackOverflowQuestions.values().stream()
                .flatMap(Set::stream)
                .toList();
    }

    @Override
    public void updateLastChecked(long chatId, GitHubRepoResponse response) {
        trackedGitHubRepos.getOrDefault(chatId, Set.of()).stream()
                .filter(repo -> repo.url().equals(response.url()))
                .forEach(repo -> {
                    repo.lastUpdated(Instant.parse(response.pushedAt()));
                    repo.stars(response.stars());
                    repo.forks(response.forks());
                    repo.openIssues(response.openIssues());
                });
    }

    @Override
    public void updateLastChecked(long chatId, StackOverflowQuestionResponse.QuestionItem response) {
        trackedStackOverflowQuestions.getOrDefault(chatId, Set.of()).stream()
                .filter(question -> question.url().equals(response.link()))
                .forEach(question -> {
                    question.lastUpdated(Instant.ofEpochSecond(response.lastActivityDate()));
                    question.answerCount(response.answerCount());
                    question.score(response.score());
                    question.viewCount(response.viewCount());
                });
    }
}
