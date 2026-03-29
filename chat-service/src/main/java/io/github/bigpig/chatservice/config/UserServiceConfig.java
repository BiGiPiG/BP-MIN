package io.github.bigpig.chatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UserServiceConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
