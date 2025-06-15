ALTER TABLE links
ALTER COLUMN type TYPE link_type
    USING (type::link_type);
