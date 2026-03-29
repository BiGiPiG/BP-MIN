package io.github.bigpig.chatservice.dto.request;

public record ReadMessageDto(
        Long messageId,
        Long chatId
) {
}
