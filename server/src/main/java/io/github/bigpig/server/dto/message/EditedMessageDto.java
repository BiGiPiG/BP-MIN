package io.github.bigpig.server.dto.message;

public record EditedMessageDto(
    Long messageId,
    String newContent
) {}
