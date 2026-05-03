package io.github.bigpig.userservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bigpig.userservice.dto.request.CreateProfileRequest;
import io.github.bigpig.userservice.dto.respose.ChatParticipantInfo;
import io.github.bigpig.userservice.dto.respose.InterlocutorInfoDto;
import io.github.bigpig.userservice.dto.respose.ProfileDto;
import io.github.bigpig.userservice.dto.respose.UserSummaryDto;
import io.github.bigpig.userservice.entities.Profile;
import io.github.bigpig.userservice.exceptions.AccessDeniedException;
import io.github.bigpig.userservice.repositories.ProfileRepository;
import io.github.bigpig.userservice.utils.ProfileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ObjectMapper objectMapper;
    private final ProfileConverter profileConverter;
    private final PresenceService presenceService;
    private final ProfileRepository profileRepository;

    public void createProfile(CreateProfileRequest createProfileRequest) {
        try {
            Profile newProfile = Profile
                    .builder()
                    .userId(createProfileRequest.id())
                    .email(createProfileRequest.email())
                    .username(createProfileRequest.username())
                    .nickname(createProfileRequest.nickname())
                    .build();
            profileRepository.save(newProfile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public ProfileDto getProfileByUsername(String username) {
        Profile curProfile = profileRepository.findByUsername(username);

        if (curProfile == null) {
            log.warn("Profile not found for username: '{}'", username);
            throw new RuntimeException("Профиль с username '" + username + "' не найден");
        }

        log.debug("Profile successfully loaded for username: '{}'", username);
        return objectMapper.convertValue(curProfile, ProfileDto.class);
    }

    public InterlocutorInfoDto getInterlocutorInfo(String userId, String username) {
        Profile curProfile = profileRepository.findByUsername(username);
        String status = presenceService.isUserOnline(userId) ? "online" : "offline";

        log.debug("User '{}' has status '{}'", userId, status);

        return InterlocutorInfoDto.builder()
                .nickname(curProfile.getNickname())
                .username(username)
                .bio(curProfile.getBio())
                .profileColor(curProfile.getProfileColor())
                .birthDate(curProfile.getBirthDate())
                .status(status)
                .build();
    }

    public ProfileDto updateProfile(Long userId, ProfileDto request) {
        Profile curProfile = profileRepository.findByUserId(userId);

        if (!curProfile.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        curProfile = profileConverter.patchProfile(curProfile, request);
        profileRepository.save(curProfile);
        return objectMapper.convertValue(curProfile, ProfileDto.class);
    }

    public List<UserSummaryDto> searchByUsername(String searchTerm) {
        List<Profile> profiles = profileRepository.searchByUsernameStartingWith(searchTerm);
        return profiles.stream().map(UserSummaryDto::from).toList();
    }

    public List<ChatParticipantInfo> getChatParticipantInfos(List<Long> ids) {
        List<Profile> profiles = profileRepository.findAllById(ids);
        return profiles.stream().map(ChatParticipantInfo::fromProfile).toList();
    }
}
