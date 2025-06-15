package backend.academy.bot.logging;

import backend.academy.bot.exception.ScrapperApiException;
import backend.academy.bot.exception.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebClientLoggingFilter {

    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Выполняем запрос: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    public ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info(
                    "Получен ответ: статус {} ({} {})",
                    clientResponse.statusCode().value(),
                    clientResponse.statusCode().isError() ? "Ошибка" : "Успешно",
                    clientResponse.statusCode().isError() ? "⚠" : "✅");
            return Mono.just(clientResponse);
        });
    }

    public ExchangeFilterFunction logError() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(ApiErrorResponse.class).flatMap(error -> {
                    log.error("Ошибка Scrapper API: {} - {}", error.code(), error.description());
                    return Mono.error(new ScrapperApiException(error));
                });
            }
            return Mono.just(clientResponse);
        });
    }
}
