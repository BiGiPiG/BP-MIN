package io.github.bigpig.server.util;

import io.github.bigpig.server.dto.chat.MessageDto;
import io.github.bigpig.server.entity.chat.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageMapper {

    private final MessageTimeMapper messageTimeMapper;

    public MessageDto toMessageDto(Chat.ChatMessage message) {
        return new MessageDto(
                message.getId(),
                message.getChat().getId(),
                message.getSenderId(),
                message.getContent(),
                message.getSentAt(),
                messageTimeMapper.fullTimeToString(message.getSentAt())
        );
    }
}
