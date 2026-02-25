package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.ProfileDto;
import io.github.bigpig.server.dto.chat.InterlocutorInfoDto;
import io.github.bigpig.server.entity.user.Profile;
import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.exceptions.AppException;
import io.github.bigpig.server.exceptions.ErrorCode;
import io.github.bigpig.server.util.InterlocutorInfoMapper;
import io.github.bigpig.server.util.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbProfileService implements ProfileService {

    private final PresenceService presenceService;
    private final UserService userService;
    private final ProfileMapper profileMapper;
    private final InterlocutorInfoMapper interlocutorInfoMapper;

    public ProfileDto getProfileByUsername(String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return profileMapper.buildProfileDto(user, user.getProfile());
    }

    public InterlocutorInfoDto getInterlocutorInfo(String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String status = presenceService.getStatus(user.getId());
        return interlocutorInfoMapper.getInterlocutorInfo(user, status);
    }

    public ProfileDto updateProfile(ProfileDto dto) {
        User user = userService.findByUsername(dto.username())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setNickname(dto.nickname());
        user.setUsername(dto.username());

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            user.setProfile(profile);
        }

        profile.setBio(dto.bio());
        profile.setBirthDate(dto.birthDate());
        profile.setProfileColor(dto.profileColor());
        userService.save(user);

        return profileMapper.buildProfileDto(user, profile);
    }
}
