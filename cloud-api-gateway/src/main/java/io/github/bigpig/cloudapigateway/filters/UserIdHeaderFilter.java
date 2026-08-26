package io.github.bigpig.cloudapigateway.filters;

import io.github.bigpig.cloudapigateway.error.ApiErrorCode;
import io.github.bigpig.cloudapigateway.error.GatewayException;
import io.github.bigpig.cloudapigateway.util.InternalHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Превращает проверенный JWT в заголовок {@link InternalHeaders#USER_ID} для сервисов
 * за gateway: сами они про JWT ничего не знают и берут пользователя из заголовка.
 * <p>
 * Схема верна ровно до тех пор, пока в сервис нельзя попасть в обход gateway.
 */
@Slf4j
@Component
@Order(-99)
public class UserIdHeaderFilter implements GlobalFilter {

    private static final String USER_ID_CLAIM = "userId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerWebExchange sanitized = stripClientUserId(exchange);

        return ReactiveSecurityContextHolder.getContext()
                .map(context -> Optional.ofNullable(context.getAuthentication()))
                .defaultIfEmpty(Optional.empty())
                .flatMap(authentication -> propagate(authentication, sanitized, chain));
    }

    private Mono<Void> propagate(Optional<Authentication> maybeAuthentication,
                                 ServerWebExchange exchange,
                                 GatewayFilterChain chain) {

        Authentication authentication = maybeAuthentication
                .filter(Authentication::isAuthenticated)
                .orElse(null);

        if (authentication == null) {
            // permitAll-маршрут (/api/auth/**): security пропустила запрос без аутентификации,
            // сервису идентичность не нужна.
            return chain.filter(exchange);
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, ApiErrorCode.TOKEN_INVALID,
                    "Authentication is not backed by a JWT"));
        }

        String userId = jwt.getClaimAsString(USER_ID_CLAIM);
        if (!StringUtils.hasText(userId)) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, ApiErrorCode.TOKEN_INVALID,
                    String.format("Token carries no %s claim", USER_ID_CLAIM)));
        }

        log.debug("Resolved {} for {}", InternalHeaders.USER_ID, exchange.getRequest().getPath());

        return chain.filter(exchange.mutate()
                .request(request -> request.header(InternalHeaders.USER_ID, userId))
                .build());
    }

    private ServerWebExchange stripClientUserId(ServerWebExchange exchange) {
        if (!exchange.getRequest().getHeaders().containsHeader(InternalHeaders.USER_ID)) {
            return exchange;
        }

        log.warn("Client sent {} for {}, header dropped",
                InternalHeaders.USER_ID, exchange.getRequest().getPath());

        return exchange.mutate()
                .request(request -> request.headers(headers -> headers.remove(InternalHeaders.USER_ID)))
                .build();
    }
}
