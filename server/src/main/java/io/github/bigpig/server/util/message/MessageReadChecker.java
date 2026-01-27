package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageReadChecker implements MessageChecker {
    @Override
    public boolean checkErrors(Long readerId, Message message) {
        return message.getChat().getParticipants().stream().noneMatch(p -> p.getUser().getId().equals(readerId));
    }
}
