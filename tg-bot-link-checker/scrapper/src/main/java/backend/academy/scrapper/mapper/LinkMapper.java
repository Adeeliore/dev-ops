package backend.academy.scrapper.mapper;

import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.model.Tag;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LinkMapper {

    @Mapping(target = "id", expression = "java(link != null ? link.linkId() : null)")
    @Mapping(target = "url", expression = "java(link != null ? link.url() : null)")
    @Mapping(
            target = "tags",
            expression =
                    "java(tags != null ? tags.stream().map(Tag::name).collect(java.util.stream.Collectors.toSet()) : null)")
    @Mapping(target = "filters", source = "filters")
    LinkResponse toLinkResponse(Link link, Set<Tag> tags, Set<String> filters);
}
