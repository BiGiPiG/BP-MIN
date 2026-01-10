package io.github.bigpig.server.dto;

public record ProfileDto(
        String nickname,
        String username,
        String birthDate,
        String bio
) {}
