package backend.academy.bot.controller;

import backend.academy.bot.api.BotApi;
import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.impl.UpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BotController implements BotApi {
    private final UpdateService updateService;

    @Override
    public ResponseEntity<Void> receiveUpdate(@RequestBody LinkUpdate update) {
        updateService.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
