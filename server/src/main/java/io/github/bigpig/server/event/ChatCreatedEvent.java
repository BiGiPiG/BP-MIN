package io.github.bigpig.server.event;

import io.github.bigpig.server.entity.Chat;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatCreatedEvent extends ApplicationEvent {
    private final Chat chat;

    public ChatCreatedEvent(Object source, Chat chat) {
        super(source);
        this.chat = chat;
    }
}