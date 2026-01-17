package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.ProfileDto;

public interface ProfileService {
    ProfileDto getProfileByUsername(String username);
    ProfileDto updateProfile(ProfileDto dto);
}
