package backend.academy.bot.state;

public enum UserState {
    NONE,
    AWAITING_LINK,
    AWAITING_TAGS,
    AWAITING_FILTERS,
    AWAITING_UNTRACK;

    public boolean isWaitingForInput() {
        return this != NONE;
    }
}
