package backend.academy.scrapper.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverflowQuestionResponse(@JsonProperty("items") List<QuestionItem> items) {
    public record QuestionItem(
            @JsonProperty("question_id") Long questionId,
            @JsonProperty("title") String title,
            @JsonProperty("link") String link,
            @JsonProperty("last_activity_date") Long lastActivityDate,
            @JsonProperty("answer_count") int answerCount,
            @JsonProperty("score") int score,
            @JsonProperty("view_count") int viewCount) {}
}
