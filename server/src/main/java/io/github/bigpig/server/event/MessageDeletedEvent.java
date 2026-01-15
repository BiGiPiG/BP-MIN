package io.github.bigpig.server.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageDeletedEvent extends ApplicationEvent {
    private final Long messageId;
    private final Long chatId;

    public MessageDeletedEvent(Object source, Long messageId, Long chatId) {
        super(source);
        this.messageId = messageId;
        this.chatId = chatId;
    }
}