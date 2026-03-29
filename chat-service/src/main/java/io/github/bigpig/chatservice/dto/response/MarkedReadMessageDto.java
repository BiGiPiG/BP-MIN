package io.github.bigpig.chatservice.dto.response;

public record MarkedReadMessageDto(
        Long chatId,
        Long messageId
) {
}
