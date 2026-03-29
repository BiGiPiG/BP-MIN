package io.github.bigpig.chatservice.dto.response;

import io.github.bigpig.chatservice.dto.ParticipantInfo;
import io.github.bigpig.chatservice.entity.ChatType;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatDto(
        Long id,
        ChatType type,
        String title,
        String lastActivity,                    // дата последнего сообщения или создания чата
        String lastMessagePreview,              // текст последнего сообщения
        boolean unread,                         // есть ли непрочитанные сообщения
        List<ParticipantInfo> participantInfo   // участники
) {}
