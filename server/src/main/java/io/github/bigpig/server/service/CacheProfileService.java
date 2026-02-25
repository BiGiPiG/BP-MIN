package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.ProfileDto;
import io.github.bigpig.server.dto.chat.InterlocutorInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class CacheProfileService implements ProfileService {

    private final String CACHE_PREFIX = "cache_";

    private final DbProfileService dbProfileService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProfileDto getProfileByUsername(String username) {
        String key = CACHE_PREFIX + username;

        if (redisTemplate.hasKey(key)) {
            log.info("profile is found in cache for username {}", username);
            return (ProfileDto) redisTemplate.opsForValue().get(key);
        }

        log.info("profile is not found in cache for username {}", username);
        ProfileDto profile = dbProfileService.getProfileByUsername(username);
        redisTemplate.opsForValue().set(key, profile);
        log.info("profile set in cache for username {}", username);

        return profile;
    }

    public InterlocutorInfoDto getInterlocutorInfo(String username) {
        return dbProfileService.getInterlocutorInfo(username);
    }

    public ProfileDto updateProfile(ProfileDto dto) {
        String key = CACHE_PREFIX + dto.username();
        if (redisTemplate.hasKey(key)) {
            log.info("profile is deleted from cache for username {}", dto);
            redisTemplate.delete(key);
        }

        return dbProfileService.updateProfile(dto);
    }
}
