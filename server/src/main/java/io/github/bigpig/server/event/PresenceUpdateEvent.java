package io.github.bigpig.server.event;

import io.github.bigpig.server.dto.chat.PresenceUpdateDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PresenceUpdateEvent extends ApplicationEvent {
    private final Long chatId;
    private final PresenceUpdateDto presenceUpdateDto;

    public PresenceUpdateEvent(Object source, Long chatId, PresenceUpdateDto presenceUpdateDto) {
        super(source);
        this.chatId = chatId;
        this.presenceUpdateDto = presenceUpdateDto;
    }
}
