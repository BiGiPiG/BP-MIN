// io.github.bigpig.server.dto.ChatDto
package io.github.bigpig.server.dto;

import io.github.bigpig.server.entity.Chat;
import io.github.bigpig.server.entity.ChatMessage;
import io.github.bigpig.server.entity.ChatParticipant;
import io.github.bigpig.server.entity.ChatType;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record ChatDto(
        Long id,
        ChatType type,
        String title,
        String lastActivity, // дата последнего сообщения или создания чата
        String lastMessagePreview,  // текст последнего сообщения (урезанный)
        boolean unread,             // есть ли непрочитанные сообщения
        List<ParticipantInfo> participantInfo   // ID участников (опционально, для групп)
) {
    public static ChatDto fromChat(Chat chat, List<ParticipantInfo> participantInfo) {
        ChatMessage lastMessage = chat.getMessages().stream()
                .max(Comparator.comparing(ChatMessage::getSentAt))
                .orElse(null);

        String lastMessagePreview = (lastMessage != null)
                ? truncateMessage(lastMessage.getContent())
                : null;

        LocalDateTime lastActivity = (lastMessage != null)
                ? lastMessage.getSentAt()
                : chat.getCreatedAt();

        boolean hasUnread = lastMessage != null;

        return new ChatDto(
                chat.getId(),
                chat.getType(),
                chat.getType() == ChatType.GROUP ? chat.getTitle() : null,
                "12:30",
                lastMessagePreview,
                hasUnread,
                participantInfo
        );
    }

    private static String truncateMessage(String content) {
        if (content == null) return null;
        return content.length() > 50
                ? content.substring(0, 50 - 3) + "..."
                : content;
    }
}
