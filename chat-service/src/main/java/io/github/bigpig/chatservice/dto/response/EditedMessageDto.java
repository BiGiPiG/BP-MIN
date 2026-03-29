package io.github.bigpig.chatservice.dto.response;

public record EditedMessageDto(
        Long messageId,
        String newContent
) {
}
