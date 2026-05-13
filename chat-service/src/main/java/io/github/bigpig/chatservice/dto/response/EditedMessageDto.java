package io.github.bigpig.chatservice.dto.response;

import lombok.Builder;

@Builder
public record EditedMessageDto(
        Long messageId,
        String newContent
) {
}
