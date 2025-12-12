package io.github.bigpig.server.dto.chat;

import io.github.bigpig.server.entity.chat.ChatType;
import java.util.List;

public record ChatDto(
        Long id,
        ChatType type,
        String title,
        String lastActivity,                    // дата последнего сообщения или создания чата
        String lastMessagePreview,              // текст последнего сообщения
        boolean unread,                         // есть ли непрочитанные сообщения
        List<ParticipantInfo> participantInfo   // ID участников
) {}
