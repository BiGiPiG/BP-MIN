package io.github.bigpig.server.dto.message;

public record DeleteMessageDto(
        Long messageId,
        Long chatId
) {}
