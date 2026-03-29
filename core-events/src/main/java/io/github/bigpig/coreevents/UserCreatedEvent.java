package io.github.bigpig.coreevents;

import lombok.Builder;

@Builder
public record UserCreatedEvent(
        Long id,
        String email,
        String nickname,
        String username
) {
}
