package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.client.GitHubClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import backend.academy.scrapper.mapper.TrackedLinkMapper;
import backend.academy.scrapper.repository.LinkRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkProcessor {

    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final LinkRepository linkRepository;
    private final TrackedLinkMapper trackedLinkMapper;

    public LinkResponse processLink(long chatId, String link, Set<String> tags, Set<String> filters) {
        if (link.contains("github.com")) {
            String owner = getOwner(link);
            String repoName = getRepoName(link);
            GitHubRepoResponse response =
                    gitHubClient.fetchRepoInfo(owner, repoName).block();

            if (response != null) {
                linkRepository.addGitHubRepo(chatId, response, tags, filters);
                return trackedLinkMapper.toDto(trackedLinkMapper.toTrackedGitHubRepo(chatId, response));
            }
        } else if (link.contains("stackoverflow.com/questions/")) {
            long questionId = getQuestionId(link);
            StackOverflowQuestionResponse response =
                    stackOverflowClient.fetchQuestionInfo(questionId).block();

            if (response != null && !response.items().isEmpty()) {
                StackOverflowQuestionResponse.QuestionItem item =
                        response.items().getFirst();
                linkRepository.addStackOverflowQuestion(chatId, item, tags, filters);
                return trackedLinkMapper.toDto(trackedLinkMapper.toTrackedStackOverflowQuestion(chatId, item));
            }
        }
        throw new IllegalArgumentException("Неподдерживаемый тип ссылки");
    }

    private String getOwner(String url) {
        return url.split("/")[3];
    }

    private String getRepoName(String url) {
        return url.split("/")[4];
    }

    private long getQuestionId(String url) {
        return Long.parseLong(url.split("/")[4]);
    }
}
