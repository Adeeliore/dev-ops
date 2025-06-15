package backend.academy.scrapper.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_links")
@Getter
@Setter
public class ChatLink {
    @EmbeddedId
    private ChatLinkId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("linkId")
    @JoinColumn(name = "link_id")
    private Link link;

    @ManyToMany
    @JoinTable(
            name = "chat_link_tags",
            joinColumns = {@JoinColumn(name = "chat_id"), @JoinColumn(name = "link_id")},
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "chat_link_filters",
            joinColumns = {@JoinColumn(name = "chat_id"), @JoinColumn(name = "link_id")})
    @Column(name = "filter")
    private Set<String> filters = new HashSet<>();
}
