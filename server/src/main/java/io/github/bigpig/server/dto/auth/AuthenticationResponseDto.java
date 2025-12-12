package io.github.bigpig.server.dto.auth;

public record AuthenticationResponseDto (
        String accessToken,
        String refreshToken
) {}
