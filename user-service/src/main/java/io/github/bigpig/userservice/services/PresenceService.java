package io.github.bigpig.userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private static final String CACHE_KEY_PREFIX = "status:";

    private final StringRedisTemplate stringRedisTemplate;

    public void updateStatus(String id, String status) {
        String key = CACHE_KEY_PREFIX + id;
        if (status.equals("online")) {
            stringRedisTemplate.opsForValue().set(key, status);
        } else {
            stringRedisTemplate.opsForValue().getAndDelete(id);
        }
    }

    public boolean isUserOnline(String id) {
        String key = CACHE_KEY_PREFIX + id;
        return stringRedisTemplate.hasKey(key);
    }


}
