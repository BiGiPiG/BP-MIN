package io.github.bigpig.chatservice.dto.request;

public record DeleteMessageDto(
        Long messageId,
        Long chatId
) {
}
