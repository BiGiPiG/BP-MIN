package io.github.bigpig.server.dto.message;

public record EditMessageDto(
    Long messageId,
    Long chatId,
    String newContent
) {}
