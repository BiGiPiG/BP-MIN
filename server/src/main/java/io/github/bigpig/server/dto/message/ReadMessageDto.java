package io.github.bigpig.server.dto.message;

public record ReadMessageDto(
    Long messageId,
    Long chatId
) {}
