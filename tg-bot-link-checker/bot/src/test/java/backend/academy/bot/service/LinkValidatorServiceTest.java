package backend.academy.bot.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.bot.service.impl.LinkValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkValidatorServiceTest {

    private final LinkValidatorService linkValidatorService = new LinkValidatorService();

    @Test
    @DisplayName("Корректный GitHub URL")
    void shouldValidateCorrectGitHubUrl() {
        assertTrue(linkValidatorService.isValid("https://github.com/owner/repo"));
    }

    @Test
    @DisplayName("Корректный StackOverflow URL")
    void shouldValidateCorrectStackOverflowUrl() {
        assertTrue(linkValidatorService.isValid("https://stackoverflow.com/questions/12345"));
    }

    @Test
    @DisplayName("Некорректный URL")
    void shouldInvalidateIncorrectUrl() {
        assertFalse(linkValidatorService.isValid("https://example.com"));
    }

    @Test
    @DisplayName("Некорректный URL без протокола")
    void shouldInvalidateUrlWithoutProtocol() {
        assertFalse(linkValidatorService.isValid("github.com/owner/repo"));
    }
}
