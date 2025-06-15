CREATE TABLE chat_links (
    chat_id BIGINT,
    link_id BIGINT,
    PRIMARY KEY (chat_id, link_id),
    FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES links(link_id) ON DELETE CASCADE
);
CREATE INDEX idx_chat_links_chat_id ON chat_links(chat_id);
CREATE INDEX idx_chat_links_link_id ON chat_links(link_id);