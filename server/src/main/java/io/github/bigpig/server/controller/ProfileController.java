package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.ProfileDto;
import io.github.bigpig.server.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable String username) {
        log.info("Fetching public profile for username: {}", username);
        ProfileDto profile = profileService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<ProfileDto> updateUserProfile(@PathVariable String username, @RequestBody ProfileDto profileDto) {
        log.info("Updating profile for user : {}", username);
        ProfileDto newProfile = profileService.updateProfile(profileDto);
        return ResponseEntity.ok(newProfile);
    }
}
