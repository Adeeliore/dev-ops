package backend.academy.scrapper.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.exception.DuplicateLinkException;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.exception.response.ApiErrorResponse;
import backend.academy.scrapper.repository.BaseTest;
import backend.academy.scrapper.service.TrackingService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ScrapperControllerIT extends BaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private TrackingService trackingService;

    @Test
    @DisplayName("Регистрация чата - 200 OK")
    void shouldRegisterChatSuccessfully() {
        long chatId = 123L;
        doNothing().when(trackingService).registerChat(chatId);

        ResponseEntity<Void> response = restTemplate.postForEntity("/api/v1/tg-chat/" + chatId, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trackingService).registerChat(chatId);
    }

    @Test
    @DisplayName("Удаление чата - 200 OK")
    void shouldDeleteChatSuccessfully() {
        long chatId = 123L;
        doNothing().when(trackingService).deleteChat(chatId);

        ResponseEntity<Void> response =
                restTemplate.exchange("/api/v1/tg-chat/" + chatId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trackingService).deleteChat(chatId);
    }

    @Test
    @DisplayName("Добавление ссылки - 200 OK")
    void shouldAddLinkSuccessfully() {
        long chatId = 123L;
        AddLinkRequest request = new AddLinkRequest("https://github.com/test/repo", Set.of("java"), Set.of("open"));

        LinkResponse expectedResponse = new LinkResponse(chatId, request.link(), request.tags(), request.filters());
        when(trackingService.addLink(chatId, request)).thenReturn(expectedResponse);

        ResponseEntity<LinkResponse> response = restTemplate.postForEntity(
                "/api/v1/links", new HttpEntity<>(request, headers(chatId)), LinkResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("Удаление ссылки - 200 OK")
    void shouldRemoveLinkSuccessfully() {
        long chatId = 123L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://github.com/test/repo");
        LinkResponse expectedResponse = new LinkResponse(chatId, request.link(), Set.of(), Set.of());

        when(trackingService.removeLink(chatId, request)).thenReturn(expectedResponse);

        ResponseEntity<LinkResponse> response = restTemplate.exchange(
                "/api/v1/links", HttpMethod.DELETE, new HttpEntity<>(request, headers(chatId)), LinkResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("Получение списка ссылок - 200 OK")
    void shouldGetTrackedLinksSuccessfully() {
        long chatId = 123L;
        List<LinkResponse> links =
                List.of(new LinkResponse(chatId, "https://github.com/test/repo", Set.of("java"), Set.of("open")));
        ListLinksResponse expectedResponse = new ListLinksResponse(links, links.size());

        when(trackingService.getTrackedLinks(chatId)).thenReturn(expectedResponse);

        ResponseEntity<ListLinksResponse> response = restTemplate.exchange(
                "/api/v1/links", HttpMethod.GET, new HttpEntity<>(headers(chatId)), ListLinksResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("Получение списка ссылок без заголовка Tg-Chat-Id - 400 Bad Request")
    void shouldReturn400WhenMissingTgChatIdHeader() {
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
                "/api/v1/links", HttpMethod.GET, new HttpEntity<>(headers), ApiErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400_BAD_REQUEST", response.getBody().code());
        assertTrue(response.getBody().description().contains("Tg-Chat-Id"));
    }

    @Test
    @DisplayName("Удаление несуществующей ссылки - 404 Not Found")
    void shouldReturn404WhenRemovingNonExistentLink() {
        long chatId = 123L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://github.com/test/non-existent-repo");

        doThrow(new LinkNotFoundException(request.link())).when(trackingService).removeLink(chatId, request);

        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
                "/api/v1/links", HttpMethod.DELETE, new HttpEntity<>(request, headers(chatId)), ApiErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("404_NOT_FOUND", response.getBody().code());
        assertEquals("Ссылка не найдена: " + request.link(), response.getBody().exceptionMessage());
    }

    @Test
    @DisplayName("Добавление уже существующей ссылки - 400 Bad Request")
    void shouldReturn400WhenAddingDuplicateLink() {
        long chatId = 123L;
        AddLinkRequest request = new AddLinkRequest("https://github.com/test/repo", Set.of("java"), Set.of("open"));

        doThrow(new DuplicateLinkException(request.link()))
                .when(trackingService)
                .addLink(chatId, request);

        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(
                "/api/v1/links", new HttpEntity<>(request, headers(chatId)), ApiErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400_BAD_REQUEST", response.getBody().code());
        assertEquals("Ссылка уже отслеживается", response.getBody().description());
    }

    private HttpHeaders headers(long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Tg-Chat-Id", String.valueOf(chatId));
        return headers;
    }
}
