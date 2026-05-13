package io.github.bigpig.chatservice.utils;

import io.github.bigpig.chatservice.dto.response.MessageDto;
import io.github.bigpig.chatservice.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final MessageTimeMapper messageTimeMapper;

    public MessageDto toMessageDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getChatParticipant().getUserId())
                .content(message.getContent())
                .shortSentAt(message.getSentAt())
                .fullSentAt(messageTimeMapper.fullTimeToString(message.getSentAt()))
                .isRead(message.getIsRead())
                .build();
    }
}
