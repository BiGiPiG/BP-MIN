package io.github.bigpig.chatservice.dto.request;

import lombok.Builder;

@Builder
public record EditMessageDto(
        Long messageId,
        Long chatId,
        String newContent
) {
}
