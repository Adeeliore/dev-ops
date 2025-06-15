package backend.academy.scrapper.repository;

import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import backend.academy.scrapper.model.TrackedGitHubRepo;
import backend.academy.scrapper.model.TrackedStackOverflowQuestion;
import java.util.List;
import java.util.Set;

public interface LinkRepository {
    void initChat(long chatId);

    void clearChat(long chatId);

    boolean chatExists(long chatId);

    void addGitHubRepo(long chatId, GitHubRepoResponse response, Set<String> tags, Set<String> filters);

    void addStackOverflowQuestion(
            long chatId, StackOverflowQuestionResponse.QuestionItem question, Set<String> tags, Set<String> filters);

    boolean isLinkTracked(long chatId, String link);

    boolean removeLink(long chatId, String url);

    List<TrackedGitHubRepo> getGitHubLinksByChatId(long chatId);

    List<TrackedStackOverflowQuestion> getStackOverflowLinksByChatId(long chatId);

    List<TrackedGitHubRepo> getAllGitHubLinks();

    List<TrackedStackOverflowQuestion> getAllStackOverflowLinks();

    void updateLastChecked(long chatId, GitHubRepoResponse response);

    void updateLastChecked(long chatId, StackOverflowQuestionResponse.QuestionItem response);
}
