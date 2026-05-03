package io.github.bigpig.userservice.controllers;

import io.github.bigpig.userservice.dto.respose.ChatParticipantInfo;
import io.github.bigpig.userservice.dto.respose.InterlocutorInfoDto;
import io.github.bigpig.userservice.dto.respose.ProfileDto;
import io.github.bigpig.userservice.dto.respose.UserSummaryDto;
import io.github.bigpig.userservice.services.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryDto>> searchUsers(@RequestParam String searchTerm) {
        log.info("Searching for users with term: {}", searchTerm);
        return ResponseEntity.ok(profileService.searchByUsername(searchTerm));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable String username) {
        log.info("Fetching public profile for username: {}", username);
        ProfileDto profile = profileService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/interlocutor-info/{username}")
    public ResponseEntity<InterlocutorInfoDto> getInterlocutorInfo(@RequestHeader("User-Id") String userId, @PathVariable String username) {
        log.info("Fetching interlocutor info for username: {}", username);
        InterlocutorInfoDto interlocutor = profileService.getInterlocutorInfo(userId, username);
        return ResponseEntity.ok(interlocutor);
    }

    @PutMapping("/{username}")
    public ResponseEntity<ProfileDto> updateUserProfile(
            @RequestHeader("User-Id") Long userId,
            @PathVariable String username,
            @RequestBody ProfileDto profileDto) {
        log.info("Updating profile for user : {}", username);
        ProfileDto newProfile = profileService.updateProfile(userId, profileDto);
        return ResponseEntity.ok(newProfile);
    }

    @GetMapping("/users/participant-infos")
    public ResponseEntity<List<ChatParticipantInfo>> getChatParticipantInfos(@RequestParam List<Long> userIds) {
        return ResponseEntity.ok(profileService.getChatParticipantInfos(userIds));
    }
}