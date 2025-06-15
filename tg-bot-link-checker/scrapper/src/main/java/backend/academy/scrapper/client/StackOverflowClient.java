package backend.academy.scrapper.client;

import backend.academy.scrapper.dto.client.Answer;
import backend.academy.scrapper.dto.client.Comment;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import java.time.Instant;
import java.util.List;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<StackOverflowQuestionResponse> fetchQuestionInfo(long questionId);

    Mono<List<Answer>> fetchAnswers(long questionId, Instant since);

    Mono<List<Comment>> fetchComments(long questionId, Instant since);
}
