package io.github.bigpig.userservice.utils;

import io.github.bigpig.userservice.dto.respose.ProfileDto;
import io.github.bigpig.userservice.entities.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter {

    public Profile patchProfile(Profile profile, ProfileDto profileDto) {
        if (profileDto.nickname() != null) profile.setNickname(profileDto.nickname());
        if (profileDto.username() != null) profile.setUsername(profileDto.username());
        if (profileDto.bio() != null) profile.setBio(profileDto.bio());
        if (profileDto.profileColor() != null) profile.setProfileColor(profileDto.profileColor());
        if (profileDto.birthDate() != null) profile.setBirthDate(profileDto.birthDate());
        return profile;
    }
}
