package io.github.bigpig.auth_service.dto.response;

import lombok.Builder;

@Builder
public record SignupResponse(
        Long id,
        String username,
        String nickname,
        String email
) {
}
