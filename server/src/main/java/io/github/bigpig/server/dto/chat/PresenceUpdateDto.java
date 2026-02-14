package io.github.bigpig.server.dto.chat;

import lombok.Builder;

@Builder
public record PresenceUpdateDto(
    Long userId,
    String username,
    String nickname,
    String status
) {}
