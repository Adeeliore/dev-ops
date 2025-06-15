CREATE TABLE chat_link_filters (
    chat_id BIGINT,
    link_id BIGINT,
    filter VARCHAR(50),
    PRIMARY KEY (chat_id, link_id, filter),
    FOREIGN KEY (chat_id, link_id) REFERENCES chat_links(chat_id, link_id) ON DELETE CASCADE
);
CREATE INDEX idx_chat_link_filters_chat_link ON chat_link_filters(chat_id, link_id);