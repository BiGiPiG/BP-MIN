package io.github.bigpig.chatservice.dto.events;

import io.github.bigpig.chatservice.entity.Chat;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ChatCreatedEvent extends ApplicationEvent {

    List<Long> ParticipantsIds;
    Chat chat;

    public ChatCreatedEvent(Object source, Chat chat, List<Long> participantsIds) {
        super(source);
        this.chat = chat;
        this.ParticipantsIds = participantsIds;
    }
}
