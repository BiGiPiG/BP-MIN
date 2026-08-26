package io.github.bigpig.cloudapigateway.config;

import io.github.bigpig.cloudapigateway.error.ApiErrorCode;
import io.github.bigpig.cloudapigateway.error.ApiErrorWriter;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;

import javax.crypto.SecretKey;

@Configuration
public class SecurityConfig {

    @Bean
    ReactiveJwtDecoder jwtDecoder(@Value("${jwt.secret.key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);
        return NimbusReactiveJwtDecoder
                .withSecretKey(signingKey)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }

    @Bean
    public SecurityWebFilterChain customFilterChain(ServerHttpSecurity http,
                                                    ReactiveJwtDecoder jwtDecoder,
                                                    ApiErrorWriter errorWriter) {

        // Spring Security отвечает на неудачную аутентификацию сама, минуя
        // GatewayErrorWebExceptionHandler: дефолтный BearerTokenServerAuthenticationEntryPoint
        // отдал бы 401 с пустым телом, а схема ErrorResponse из api/openapi.yaml обещает тело.
        // Одной регистрации достаточно: oauth2ResourceServer ставит entry point и в
        // AuthenticationWebFilter, и в ExceptionTranslationWebFilter — отдельный
        // .exceptionHandling() ничего не меняет (проверено).
        ServerAuthenticationEntryPoint entryPoint = (exchange, exception) -> errorWriter.write(
                exchange,
                HttpStatus.UNAUTHORIZED,
                ApiErrorCode.UNAUTHORIZED,
                "Missing or invalid access token");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(authorize -> authorize
                                .pathMatchers("/api/auth/**").permitAll()
                                .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(entryPoint)
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder))
                )
                .build();
    }
}
