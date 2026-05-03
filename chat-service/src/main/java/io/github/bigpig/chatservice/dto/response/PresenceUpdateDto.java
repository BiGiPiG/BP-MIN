package io.github.bigpig.chatservice.dto.response;

import lombok.Builder;

@Builder
public record PresenceUpdateDto(
        Long userId,
        String status
) {
}
