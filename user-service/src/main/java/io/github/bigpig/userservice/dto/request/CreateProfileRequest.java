package io.github.bigpig.userservice.dto.request;

import lombok.Builder;

@Builder
public record CreateProfileRequest(
        Long id,
        String username,
        String nickname,
        String email
) {
}
