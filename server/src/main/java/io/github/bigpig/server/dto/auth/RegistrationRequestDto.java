package io.github.bigpig.server.dto.auth;

public record RegistrationRequestDto (
        String username,
        String email,
        String password
) {}
