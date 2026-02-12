package io.github.bigpig.server.dto;

import io.github.bigpig.server.entity.user.ProfileColor;

public record ProfileDto(
        String nickname,
        String username,
        String birthDate,
        String bio,
        ProfileColor profileColor
) {}
