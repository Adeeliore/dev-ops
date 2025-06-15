package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.dto.enumeration.LinkType;
import backend.academy.scrapper.dto.request.AddLinkRequest;
import backend.academy.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.LinkResponse;
import backend.academy.scrapper.dto.response.ListLinksResponse;
import backend.academy.scrapper.exception.ChatNotRegisteredException;
import backend.academy.scrapper.mapper.LinkMapper;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.model.ChatLink;
import backend.academy.scrapper.model.ChatLinkId;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.model.Tag;
import backend.academy.scrapper.repository.interfaces.ChatLinkRepository;
import backend.academy.scrapper.repository.interfaces.ChatRepository;
import backend.academy.scrapper.repository.interfaces.LinkRepository;
import backend.academy.scrapper.repository.interfaces.TagRepository;
import backend.academy.scrapper.service.TrackingService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final TagRepository tagRepository;
    private final LinkMapper linkMapper;

    @Override
    @Transactional
    public void registerChat(long id) {
        Chat chat = new Chat();
        chat.chatId(id);
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void deleteChat(long id) {
        chatRepository.deleteById(id);
    }

    @Override
    @Transactional()
    public ListLinksResponse getTrackedLinks(long chatId) {
        List<ChatLink> chatLinks = chatLinkRepository.findByChatId(chatId);
        List<LinkResponse> links = chatLinks.stream()
                .map(cl -> linkMapper.toLinkResponse(cl.link(), cl.tags(), cl.filters()))
                .toList();
        return new ListLinksResponse(links, links.size());
    }

    @Override
    @Transactional
    public LinkResponse addLink(long chatId, AddLinkRequest request) {
        Chat chat = chatRepository.findById(chatId);
        if (chat == null) {
            throw new ChatNotRegisteredException(chatId);
        }

        Link link = linkRepository.findByUrl(request.link());
        if (link == null) {
            link = new Link();
            link.url(request.link());
            link.type(determineLinkType(request.link()));
            link = linkRepository.save(link);
        }

        ChatLink chatLink = new ChatLink();
        ChatLinkId id = new ChatLinkId();
        id.chatId(chatId);
        id.linkId(link.linkId());
        chatLink.id(id);
        chatLink.chat(chat);
        chatLink.link(link);

        Set<Tag> tags = request.tags().stream()
                .map(name -> {
                    Tag tag = tagRepository.findByName(name);
                    if (tag == null) {
                        tag = new Tag();
                        tag.name(name);
                        tag = tagRepository.save(tag);
                    }
                    return tag;
                })
                .collect(Collectors.toSet());
        chatLink.tags(tags);
        chatLink.filters(request.filters());

        chatLinkRepository.save(chatLink);
        return linkMapper.toLinkResponse(link, tags, request.filters());
    }

    @Override
    @Transactional
    public LinkResponse removeLink(long chatId, RemoveLinkRequest request) {
        Link link = linkRepository.findByUrl(request.link());
        if (link == null) {
            throw new IllegalArgumentException("Link not found: " + request.link());
        }
        chatLinkRepository.deleteByChatIdAndLinkId(chatId, link.linkId());
        return linkMapper.toLinkResponse(link, Set.of(), Set.of());
    }

    private LinkType determineLinkType(String url) {
        if (url.contains("github.com")) return LinkType.GITHUB;
        if (url.contains("stackoverflow.com")) return LinkType.STACKOVERFLOW;
        throw new IllegalArgumentException("Unsupported link type: " + url);
    }
}
