CREATE INDEX idx_participants_chat_id ON chat_participants(chat_id);
CREATE INDEX idx_participants_user_id ON chat_participants(user_id);
CREATE INDEX idx_messages_chat_sent ON messages(chat_id, sent_at DESC);
CREATE INDEX idx_messages_participant ON messages(participant_id);
