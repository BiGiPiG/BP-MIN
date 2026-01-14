package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageDeleteChecker implements IMessageDeleteChecker {
    @Override
    public boolean canDelete(long deleterId, Message message) {
        return message.getChatParticipant().getUser().getId().equals(deleterId);
    }
}
