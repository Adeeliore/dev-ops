CREATE TABLE chat_link_tags (
    chat_id BIGINT,
    link_id BIGINT,
    tag_id BIGINT,
    PRIMARY KEY (chat_id, link_id, tag_id),
    FOREIGN KEY (chat_id, link_id) REFERENCES chat_links(chat_id, link_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);
CREATE INDEX idx_chat_link_tags_chat_link ON chat_link_tags(chat_id, link_id);