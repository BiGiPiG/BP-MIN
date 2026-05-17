package io.github.bigpig.cloudapigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Value("${auth.service.path}")
    private String AUTH_SERVICE_PATH;
    @Value("${chat.service.path}")
    private String CHAT_SERVICE_PATH;
    @Value("${user.service.path}")
    private String USER_SERVICE_PATH;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //auth-service
                .route(p -> p
                        .path("/api/auth/signin")
                        .filters(f -> f.stripPrefix(2))
                        .uri(AUTH_SERVICE_PATH))
                .route(p -> p
                        .path("/api/auth/signup")
                        .filters(f -> f.stripPrefix(2))
                        .uri(AUTH_SERVICE_PATH))
                .route(p -> p
                        .path("/api/auth/refresh-token")
                        .filters(f -> f.stripPrefix(2))
                        .uri(AUTH_SERVICE_PATH))


                //chat-service
                .route(p -> p
                        .path("/api/chats")
                        .filters(f -> f.stripPrefix(2))
                        .uri(CHAT_SERVICE_PATH))
                .route(p -> p
                        .path("/api/chats/history/{chatId}")
                        .filters(f -> f.stripPrefix(2))
                        .uri(CHAT_SERVICE_PATH))

                //user-service
                .route(p -> p
                        .path("/api/profiles/{username}")
                        .filters(f -> f.stripPrefix(2))
                        .uri(USER_SERVICE_PATH))
                .route(p -> p
                        .path("/api/profiles/interlocutor-info/{username}")
                        .filters(f -> f.stripPrefix(2))
                        .uri(USER_SERVICE_PATH))
                .route(p -> p
                        .path("/api/profiles/{username}")
                        .filters(f -> f.stripPrefix(2))
                        .uri(USER_SERVICE_PATH))
                .route(p -> p
                        .path("/api/users/search")
                        .filters(f -> f.stripPrefix(2))
                        .uri(USER_SERVICE_PATH))
                .build();

    }
}
