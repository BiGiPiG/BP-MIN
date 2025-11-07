package io.github.bigpig.server.dto;

public record ExceptionResponseDto(
        String message,
        String errorCode
) {}