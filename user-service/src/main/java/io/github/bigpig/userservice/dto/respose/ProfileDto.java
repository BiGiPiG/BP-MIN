package io.github.bigpig.userservice.dto.respose;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.bigpig.userservice.entities.ProfileColor;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileDto(
        String nickname,
        String username,
        String birthDate,
        String bio,
        ProfileColor profileColor
) {
}
