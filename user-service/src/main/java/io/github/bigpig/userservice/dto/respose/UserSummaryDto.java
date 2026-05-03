package io.github.bigpig.userservice.dto.respose;

import io.github.bigpig.userservice.entities.Profile;

public record UserSummaryDto (
        Long id,
        String username,
        String nickname
) {
    public static UserSummaryDto from(Profile profile) {
        return new UserSummaryDto(
                profile.getUserId(),
                profile.getUsername(),
                profile.getNickname()
        );
    }
}
