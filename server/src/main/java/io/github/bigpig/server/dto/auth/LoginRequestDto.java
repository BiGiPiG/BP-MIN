package io.github.bigpig.server.dto.auth;

public record LoginRequestDto (
    String username,
    String password
) {}
