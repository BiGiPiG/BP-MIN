package io.github.bigpig.chatservice.dto.request;

public record EditMessageDto(
        Long messageId,
        Long chatId,
        String newContent
) {
}
