package io.github.bigpig.server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.bigpig.server.dto.ProfileDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.disable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        mapper.disable(SerializationFeature.WRITE_ENUMS_USING_INDEX);

        return mapper;
    }

    @Bean
    public RedisTemplate<String, ProfileDto> redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper
    ) {
        RedisTemplate<String, ProfileDto> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        var serializer = new Jackson2JsonRedisSerializer<>(objectMapper, ProfileDto.class);
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

}
