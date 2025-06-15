package backend.academy.scrapper.dto;

import java.util.List;

public record ExtendedLinkUpdate(
        Long id, String url, String description, List<Long> tgChatIds, String eventType, String author) {
    public ExtendedLinkUpdate(Long id, String url, String description, List<Long> tgChatIds) {
        this(id, url, description, tgChatIds, null, null);
    }
}
