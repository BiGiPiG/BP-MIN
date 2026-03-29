package io.github.bigpig.userservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bigpig.userservice.dto.respose.InterlocutorInfoDto;
import io.github.bigpig.userservice.dto.respose.ProfileDto;
import io.github.bigpig.userservice.entities.Profile;
import io.github.bigpig.userservice.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ProfileService {

    @Value("${services.chat.path}")
    private String CHAT_SERVICE;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final ProfileRepository profileRepository;

    public ProfileDto getProfileByUsername(String username) {
        Profile curProfile = profileRepository.findByUsername(username);
        return objectMapper.convertValue(curProfile, ProfileDto.class);
    }

    public InterlocutorInfoDto getInterlocutorInfo(String username) {

    }
}
