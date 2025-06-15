package backend.academy.bot.model;

import backend.academy.bot.state.UserState;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserSession {
    private final long chatId;
    private UserState state = UserState.NONE;

    private String pendingLink;
    private List<String> pendingTags = new ArrayList<>();
    private List<String> pendingFilters = new ArrayList<>();

    public void reset() {
        this.state = UserState.NONE;
        this.pendingLink = null;
        this.pendingTags = new ArrayList<>();
        this.pendingFilters = new ArrayList<>();
    }

    public void rollback() {
        switch (state) {
            case AWAITING_FILTERS -> {
                pendingFilters = new ArrayList<>();
                state = UserState.AWAITING_TAGS;
            }
            case AWAITING_TAGS -> {
                pendingTags = new ArrayList<>();
                state = UserState.AWAITING_LINK;
            }
            default -> reset();
        }
    }
}
