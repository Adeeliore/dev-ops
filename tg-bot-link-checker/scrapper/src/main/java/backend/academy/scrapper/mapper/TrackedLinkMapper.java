package backend.academy.scrapper.mapper;

import backend.academy.scrapper.dto.response.GitHubRepoResponse;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.StackOverflowQuestionResponse;
import backend.academy.scrapper.model.TrackedGitHubRepo;
import backend.academy.scrapper.model.TrackedLink;
import backend.academy.scrapper.model.TrackedStackOverflowQuestion;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TrackedLinkMapper {
    TrackedLinkMapper INSTANCE = Mappers.getMapper(TrackedLinkMapper.class);

    // Маппинг TrackedGitHubRepo -> LinkResponse
    @Mapping(target = "id", expression = "java(link.tgChatId())")
    @Mapping(target = "url", expression = "java(link.url())")
    @Mapping(target = "tags", expression = "java(link.tags())")
    @Mapping(target = "filters", expression = "java(link.filters())")
    LinkResponse toDto(TrackedLink link);

    // Маппинг GitHubRepoResponse + chatId -> TrackedGitHubRepo
    @Mapping(source = "chatId", target = "tgChatId")
    @Mapping(source = "response.url", target = "url")
    @Mapping(source = "response.pushedAt", target = "lastUpdated", qualifiedByName = "stringToInstant")
    @Mapping(source = "response.openIssues", target = "openIssues")
    @Mapping(source = "response.stars", target = "stars")
    @Mapping(source = "response.forks", target = "forks")
    @Mapping(target = "tags", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "filters", expression = "java(java.util.Collections.emptySet())")
    TrackedGitHubRepo toTrackedGitHubRepo(long chatId, GitHubRepoResponse response);

    // Маппинг StackOverflowQuestionResponse.QuestionItem + chatId -> TrackedStackOverflowQuestion
    @Mapping(source = "chatId", target = "tgChatId")
    @Mapping(source = "item.link", target = "url")
    @Mapping(source = "item.lastActivityDate", target = "lastUpdated", qualifiedByName = "longToInstant")
    @Mapping(source = "item.answerCount", target = "answerCount")
    @Mapping(source = "item.score", target = "score")
    @Mapping(source = "item.viewCount", target = "viewCount")
    @Mapping(target = "tags", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "filters", expression = "java(java.util.Collections.emptySet())")
    TrackedStackOverflowQuestion toTrackedStackOverflowQuestion(
            long chatId, StackOverflowQuestionResponse.QuestionItem item);

    // Кастомные методы для преобразования типов
    @Named("stringToInstant")
    default Instant stringToInstant(String date) {
        return date != null ? Instant.parse(date) : null;
    }

    @Named("longToInstant")
    default Instant longToInstant(Long epochSecond) {
        return epochSecond != null ? Instant.ofEpochSecond(epochSecond) : null;
    }
}
