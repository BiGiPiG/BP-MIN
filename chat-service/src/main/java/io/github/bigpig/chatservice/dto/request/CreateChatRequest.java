package io.github.bigpig.chatservice.dto.request;

import io.github.bigpig.chatservice.entity.ChatType;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateChatRequest(
        ChatType type,
        String title,
        List<Long> participantIds
) {
}
