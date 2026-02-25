package io.github.bigpig.server.dto.chat;

import io.github.bigpig.server.entity.user.ProfileColor;
import lombok.Builder;

@Builder
public record InterlocutorInfoDto (
    String nickname,
    String username,
    String birthDate,
    String bio,
    ProfileColor profileColor,
    String status
) {
}
