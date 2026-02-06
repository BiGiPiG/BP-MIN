package io.github.bigpig.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.bigpig.server.entity.user.ProfileColor;

public record ProfileDto(
        String nickname,
        String username,
        String birthDate,
        String bio,
        @JsonProperty("profileColor")
        ProfileColor profileColor
) {}
