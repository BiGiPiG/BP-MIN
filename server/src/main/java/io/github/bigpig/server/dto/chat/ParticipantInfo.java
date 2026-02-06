package io.github.bigpig.server.dto.chat;

import io.github.bigpig.server.entity.user.ProfileColor;

public record ParticipantInfo(
        Long userId,
        String nickname,
        String username,
        ProfileColor profileColor
) {}
