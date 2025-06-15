package backend.academy.scrapper.client.impl;

import backend.academy.scrapper.AppConfig;
import backend.academy.scrapper.client.AbstractApiClient;
import backend.academy.scrapper.client.StackOverflowClient;
import backend.academy.scrapper.dto.client.Answer;
import backend.academy.scrapper.dto.client.Comment;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StackOverflowClientImpl extends AbstractApiClient implements StackOverflowClient {

    private final String stackOverflowApiUrl;
    private final String key;

    public StackOverflowClientImpl(@Qualifier("stackoverflowWebClient") WebClient webClient, AppConfig appConfig) {
        super(webClient);
        this.stackOverflowApiUrl = appConfig.stackOverflowApiUrl();
        this.key = appConfig.stackOverflow().key();
    }

    @Override
    public Mono<StackOverflowQuestionResponse> fetchQuestionInfo(long questionId) {
        String uriTemplate = stackOverflowApiUrl
                + "?order=desc&sort=activity&site=stackoverflow&filter=!9_bDE(fI5)&key={key}&id={id}";
        Map<String, Object> params = Map.of(
                "key", key,
                "id", questionId);
        return fetchData(uriTemplate, params, StackOverflowQuestionResponse.class);
    }

    @Override
    public Mono<List<Answer>> fetchAnswers(long questionId, Instant since) {
        String uriTemplate = stackOverflowApiUrl
                + "/answers?order=desc&sort=activity&site=stackoverflow&filter=withbody&key={key}&id={id}";
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("id", questionId);
        if (since != null) {
            uriTemplate += "&fromdate={fromdate}";
            params.put("fromdate", since.getEpochSecond());
        }
        return fetchData(uriTemplate, params, new ParameterizedTypeReference<List<Answer>>() {});
    }

    @Override
    public Mono<List<Comment>> fetchComments(long questionId, Instant since) {
        String uriTemplate = stackOverflowApiUrl
                + "/comments?order=desc&sort=creation&site=stackoverflow&filter=withbody&key={key}&id={id}";
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("id", questionId);
        if (since != null) {
            uriTemplate += "&fromdate={fromdate}";
            params.put("fromdate", since.getEpochSecond());
        }
        return fetchData(uriTemplate, params, new ParameterizedTypeReference<List<Comment>>() {});
    }
}
