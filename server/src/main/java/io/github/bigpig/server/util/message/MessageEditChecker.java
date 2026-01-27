package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageEditChecker implements MessageChecker {
    @Override
    public boolean checkErrors(Long editorId, Message message) {
        return !message.getChatParticipant().getUser().getId().equals(editorId);
    }
}
