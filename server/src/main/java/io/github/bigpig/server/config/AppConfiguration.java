package io.github.bigpig.server.config;

import io.github.bigpig.server.dto.ProfileDto;
import io.github.bigpig.server.repository.UserRepository;
import io.github.bigpig.server.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class AppConfiguration {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public ProfileService profileService(DbProfileService dbProfileService, RedisTemplate<String, ProfileDto> redisTemplate) {
        return new CacheProfileService(dbProfileService, redisTemplate);
    }

}
