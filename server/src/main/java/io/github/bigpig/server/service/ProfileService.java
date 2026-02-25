package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.ProfileDto;
import io.github.bigpig.server.dto.chat.InterlocutorInfoDto;

public interface ProfileService {
    ProfileDto getProfileByUsername(String username);
    ProfileDto updateProfile(ProfileDto dto);
    InterlocutorInfoDto getInterlocutorInfo(String username);
}
