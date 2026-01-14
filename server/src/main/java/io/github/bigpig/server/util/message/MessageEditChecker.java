package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageEditChecker implements IMessageEditChecker {
    @Override
    public boolean canEdit(long editorId, Message message) {
        return message.getChatParticipant().getUser().getId().equals(editorId);
    }
}
