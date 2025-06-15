package backend.academy.scrapper.client;

import backend.academy.scrapper.exception.ExternalServiceException;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class AbstractApiClient {

    private final WebClient webClient;

    public AbstractApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    protected <T, P> Mono<T> fetchData(String uri, Map<String, Object> params, Class<T> responseType) {
        return webClient
                .get()
                .uri(uri, params)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response ->
                                Mono.error(new ExternalServiceException("API вернул ошибку: " + response.statusCode())))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(
                                new ExternalServiceException("API временно недоступен: " + response.statusCode())))
                .bodyToMono(responseType);
    }

    protected <T> Mono<T> fetchData(
            String uri, Map<String, Object> params, ParameterizedTypeReference<T> responseType) {
        return webClient
                .get()
                .uri(uri, params)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response ->
                                Mono.error(new ExternalServiceException("API вернул ошибку: " + response.statusCode())))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(
                                new ExternalServiceException("API временно недоступен: " + response.statusCode())))
                .bodyToMono(responseType);
    }
}
