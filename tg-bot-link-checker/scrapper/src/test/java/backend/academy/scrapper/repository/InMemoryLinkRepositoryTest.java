package backend.academy.scrapper.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import backend.academy.scrapper.model.TrackedGitHubRepo;
import backend.academy.scrapper.model.TrackedStackOverflowQuestion;
import backend.academy.scrapper.repository.impl.InMemoryLinkRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryLinkRepositoryTest {

    private final InMemoryLinkRepository repository = new InMemoryLinkRepository();

    @Test
    @DisplayName("Регистрация чата")
    void shouldRegisterChat() {
        long chatId = 123L;

        repository.initChat(chatId);

        GitHubRepoResponse response = new GitHubRepoResponse(
                12345L, "test-repo", "github.com/test/repo", "2024-03-24T12:00:00Z", "2024-03-24T12:00:00Z", 15, 10, 5);

        assertDoesNotThrow(() -> repository.addGitHubRepo(chatId, response, Set.of(), Set.of()));
    }

    @Test
    @DisplayName("Добавление GitHub-репозитория")
    void shouldAddGitHubRepo() {
        long chatId = 123L;
        repository.initChat(chatId);

        GitHubRepoResponse response = new GitHubRepoResponse(
                12345L, "test-repo", "github.com/test/repo", "2024-03-24T12:00:00Z", "2024-03-24T12:00:00Z", 15, 10, 5);

        repository.addGitHubRepo(chatId, response, Set.of("java"), Set.of("active"));
        List<TrackedGitHubRepo> repos = repository.getGitHubLinksByChatId(chatId);

        assertEquals(1, repos.size());
        assertEquals(response.url(), repos.getFirst().url());
    }

    @Test
    @DisplayName("Удаление ссылки")
    void shouldRemoveLink() {
        long chatId = 123L;
        repository.initChat(chatId);

        GitHubRepoResponse response = new GitHubRepoResponse(
                12345L, "test-repo", "github.com/test/repo", "2024-03-24T12:00:00Z", "2024-03-24T12:00:00Z", 15, 10, 5);

        repository.addGitHubRepo(chatId, response, Set.of(), Set.of());
        repository.removeLink(chatId, response.url());

        assertTrue(repository.getGitHubLinksByChatId(chatId).isEmpty());
    }

    @Test
    @DisplayName("Очистка чата")
    void shouldClearChat() {
        long chatId = 123L;
        repository.initChat(chatId);

        GitHubRepoResponse response = new GitHubRepoResponse(
                12345L, "test-repo", "github.com/test/repo", "2024-03-24T12:00:00Z", "2024-03-24T12:00:00Z", 15, 10, 5);

        repository.addGitHubRepo(chatId, response, Set.of(), Set.of());
        repository.clearChat(chatId);

        assertTrue(repository.getGitHubLinksByChatId(chatId).isEmpty());
    }

    @Test
    @DisplayName("Обновление последней проверки GitHub-репозитория")
    void shouldUpdateLastCheckedForGitHub() {
        long chatId = 123L;
        repository.initChat(chatId);

        GitHubRepoResponse response = new GitHubRepoResponse(
                12345L, "test-repo", "github.com/test/repo", "2024-03-24T12:00:00Z", "2024-03-24T12:00:00Z", 15, 10, 5);

        repository.addGitHubRepo(chatId, response, Set.of(), Set.of());

        GitHubRepoResponse updatedResponse = new GitHubRepoResponse(
                12345L, "test-repo", "github.com/test/repo", "2024-03-24T12:00:00Z", "2024-03-25T12:00:00Z", 20, 12, 7);

        repository.updateLastChecked(chatId, updatedResponse);
        TrackedGitHubRepo repo = repository.getGitHubLinksByChatId(chatId).getFirst();

        assertEquals(12, repo.stars());
        assertEquals(7, repo.forks());
        assertEquals(20, repo.openIssues());
        assertEquals(Instant.parse("2024-03-25T12:00:00Z"), repo.lastUpdated());
    }

    @Test
    @DisplayName("Добавление StackOverflow-вопроса")
    void shouldAddStackOverflowQuestion() {
        long chatId = 123L;
        repository.initChat(chatId);

        StackOverflowQuestionResponse.QuestionItem item = new StackOverflowQuestionResponse.QuestionItem(
                123L, "Test question", "stackoverflow.com/q/123", 1717181920L, 5, 10, 100);

        repository.addStackOverflowQuestion(chatId, item, Set.of("java"), Set.of("hot"));
        List<TrackedStackOverflowQuestion> questions = repository.getStackOverflowLinksByChatId(chatId);

        assertEquals(1, questions.size());
        assertEquals(item.link(), questions.getFirst().url());
    }

    @Test
    @DisplayName("Получение всех ссылок для чата")
    void shouldGetAllLinksByChatId() {
        long chatId = 123L;
        repository.initChat(chatId);

        GitHubRepoResponse repoResponse = new GitHubRepoResponse(
                12345L, "test-repo", "github.com/test/repo", "2024-03-24T12:00:00Z", "2024-03-24T12:00:00Z", 15, 10, 5);

        StackOverflowQuestionResponse.QuestionItem questionResponse = new StackOverflowQuestionResponse.QuestionItem(
                123L, "Test question", "stackoverflow.com/q/123", 1717181920L, 5, 10, 100);

        repository.addGitHubRepo(chatId, repoResponse, Set.of(), Set.of());
        repository.addStackOverflowQuestion(chatId, questionResponse, Set.of(), Set.of());

        assertEquals(1, repository.getGitHubLinksByChatId(chatId).size());
        assertEquals(1, repository.getStackOverflowLinksByChatId(chatId).size());
    }

    @Test
    @DisplayName("Проверка отслеживаемой ссылки")
    void shouldCheckIfLinkIsTracked() {
        long chatId = 123L;
        repository.initChat(chatId);

        String url = "github.com/test/repo";
        GitHubRepoResponse response = new GitHubRepoResponse(
                12345L, "test-repo", url, "2024-03-24T12:00:00Z", "2024-03-24T12:00:00Z", 15, 10, 5);

        repository.addGitHubRepo(chatId, response, Set.of(), Set.of());

        assertTrue(repository.isLinkTracked(chatId, url));
        assertFalse(repository.isLinkTracked(chatId, "github.com/other/repo"));
    }

    @Test
    @DisplayName("Обновление последней проверки StackOverflow-вопроса")
    void shouldUpdateLastCheckedForStackOverflow() {
        long chatId = 123L;
        repository.initChat(chatId);

        StackOverflowQuestionResponse.QuestionItem item = new StackOverflowQuestionResponse.QuestionItem(
                123L, "Test question", "stackoverflow.com/q/123", 1717181920L, 5, 10, 100);

        repository.addStackOverflowQuestion(chatId, item, Set.of(), Set.of());

        StackOverflowQuestionResponse.QuestionItem updatedItem = new StackOverflowQuestionResponse.QuestionItem(
                123L, "Test question", "stackoverflow.com/q/123", 1717200000L, 7, 15, 120);

        repository.updateLastChecked(chatId, updatedItem);
        TrackedStackOverflowQuestion question =
                repository.getStackOverflowLinksByChatId(chatId).getFirst();

        assertEquals(7, question.answerCount());
        assertEquals(15, question.score());
        assertEquals(120, question.viewCount());
        assertEquals(Instant.ofEpochSecond(1717200000L), question.lastUpdated());
    }
}
