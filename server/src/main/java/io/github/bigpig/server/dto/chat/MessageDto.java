package io.github.bigpig.server.dto.chat;

import io.github.bigpig.server.entity.chat.Chat;
import java.time.LocalDateTime;

public record MessageDto(
        Long chatId,               // ID чата
        Long senderId,             // ID отправителя
        String content,            // текст сообщения
        LocalDateTime sentAt       // когда отправлено
) {
    public static MessageDto of(Chat.ChatMessage message) {
        return new MessageDto(
            message.getChat().getId(),
            message.getSenderId(),
            message.getContent(),
            message.getSentAt()
        );
    }
}
