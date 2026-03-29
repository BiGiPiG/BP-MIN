package io.github.bigpig.chatservice.utils;

import io.github.bigpig.chatservice.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageReadChecker implements MessageChecker {
    @Override
    public boolean checkErrors(Long readerId, Message message) {
        return message.getChat().getParticipants().stream().noneMatch(p -> p.getUserId().equals(readerId));
    }
}
