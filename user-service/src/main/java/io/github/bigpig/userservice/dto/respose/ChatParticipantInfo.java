package io.github.bigpig.userservice.dto.respose;

import io.github.bigpig.userservice.entities.Profile;
import io.github.bigpig.userservice.entities.ProfileColor;
import lombok.Builder;

@Builder
public record ChatParticipantInfo(
        Long userId,
        String nickname,
        String username,
        ProfileColor iconColor
) {
    public static ChatParticipantInfo fromProfile(Profile curProfile) {
        return ChatParticipantInfo.builder()
                .userId(curProfile.getUserId())
                .username(curProfile.getUsername())
                .nickname(curProfile.getNickname())
                .iconColor(curProfile.getProfileColor())
                .build();
    }
}
