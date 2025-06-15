package backend.academy.scrapper.service;

import backend.academy.scrapper.dto.LinkUpdate;

public interface BotNotificationService {
    void notifyUpdate(LinkUpdate linkUpdate);
}
