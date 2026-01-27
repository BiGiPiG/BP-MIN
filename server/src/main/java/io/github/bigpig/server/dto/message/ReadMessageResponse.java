package io.github.bigpig.server.dto.message;

public record ReadMessageResponse(
    Long chatId,
    Long messageId
) {
}
