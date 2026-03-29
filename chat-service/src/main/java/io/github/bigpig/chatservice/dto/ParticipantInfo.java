package io.github.bigpig.chatservice.dto;

import io.github.bigpig.chatservice.entity.IconColor;

public record ParticipantInfo(
        Long userId,
        String nickname,
        String username,
        IconColor iconColor
) {}