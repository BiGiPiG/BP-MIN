package io.github.bigpig.server.dto.auth;

public record RegistrationRequestDto (
        String nickname,
        String username,
        String email,
        String password
) {}
