package io.github.bigpig.userservice.dto.respose;

import io.github.bigpig.userservice.entities.ProfileColor;
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
