package io.github.bigpig.userservice.dto.respose;

import io.github.bigpig.userservice.entities.ProfileColor;

public record ProfileDto(
        String nickname,
        String username,
        String birthDate,
        String bio,
        ProfileColor profileColor
) {
}
