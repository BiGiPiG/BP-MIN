package io.github.bigpig.chatservice.dto.request;

import io.github.bigpig.chatservice.entity.ChatType;

import java.util.List;

public record CreateChatRequest(
        ChatType type,
        String title,
        List<Long> participantIds
) {
}
