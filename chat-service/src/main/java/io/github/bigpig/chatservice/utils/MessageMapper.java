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
        return new MessageDto(
                message.getId(),
                message.getChat().getId(),
                message.getChatParticipant().getUserId(),
                message.getContent(),
                message.getSentAt(),
                messageTimeMapper.fullTimeToString(message.getSentAt()),
                message.getIsRead()
        );
    }
}
