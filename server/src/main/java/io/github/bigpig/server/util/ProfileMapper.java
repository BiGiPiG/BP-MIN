package io.github.bigpig.server.util;

import io.github.bigpig.server.dto.ProfileDto;
import io.github.bigpig.server.entity.user.Profile;
import io.github.bigpig.server.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    public ProfileDto buildProfileDto(User user, Profile profile) {
        return new ProfileDto(
            user.getNickname(),
            user.getUsername(),
            profile.getBirthDate().toString(),
            profile.getBio()
        );
    }
}
