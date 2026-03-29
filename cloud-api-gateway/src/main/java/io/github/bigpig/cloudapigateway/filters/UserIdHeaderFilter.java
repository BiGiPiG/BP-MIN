package io.github.bigpig.cloudapigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Order(-99)
public class UserIdHeaderFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(UserIdHeaderFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info(">>> Filter started for path: {}", exchange.getRequest().getPath());

        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(optCtx -> {
                    if (optCtx.isEmpty()) {
                        log.debug("<<< No SecurityContext found. Passing through.");
                        return chain.filter(exchange);
                    }

                    SecurityContext securityContext = optCtx.get();
                    Authentication authentication = securityContext.getAuthentication();

                    if (authentication == null || !authentication.isAuthenticated()) {
                        log.debug("<<< Not authenticated. Passing through.");
                        return chain.filter(exchange);
                    }

                    Object principal = authentication.getPrincipal();

                    if (!(principal instanceof Jwt jwt)) {
                        log.debug("<<< Principal is not JWT. Passing through.");
                        return chain.filter(exchange);
                    }

                    String userId = jwt.getClaimAsString("userId");

                    if (userId == null) {
                        log.info("<<< userId not found in token. Passing through.");
                        return chain.filter(exchange);
                    }

                    log.info(">>> Adding header 'userId': {}", userId);

                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("User-Id", userId)
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();

                    return chain.filter(mutatedExchange);
                });
    }
}