package io.github.bigpig.chatservice.utils;

import io.github.bigpig.chatservice.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageEditChecker implements MessageChecker {
    @Override
    public boolean checkErrors(Long editorId, Message message) {
        return !message.getChatParticipant().getUserId().equals(editorId);
    }
}
