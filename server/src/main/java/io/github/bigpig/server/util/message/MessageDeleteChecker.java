package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageDeleteChecker implements MessageChecker {
    @Override
    public boolean checkErrors(Long deleterId, Message message) {
        return !message.getChatParticipant().getUser().getId().equals(deleterId);
    }
}
