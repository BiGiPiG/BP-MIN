package io.github.bigpig.cloudapigateway.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

/**
 * Единственное место, где ошибка периметра превращается в тело ответа.
 * Используется и обработчиком исключений, и entry point'ом Spring Security,
 * чтобы формат не разъезжался между ними.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiErrorWriter {

    private static final byte[] FALLBACK_BODY =
            "{\"status\":500,\"code\":\"INTERNAL_ERROR\"}".getBytes(StandardCharsets.UTF_8);

    private final ObjectMapper objectMapper;

    public Mono<Void> write(ServerWebExchange exchange, HttpStatus status, ApiErrorCode code, String details) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            // Ответ уже поехал клиенту — статус менять поздно.
            log.warn("Response already committed, cannot render error {} {}", status, code);
            return Mono.empty();
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.fromSupplier(
                () -> response.bufferFactory().wrap(serialize(status, code, details))));
    }

    private byte[] serialize(HttpStatus status, ApiErrorCode code, String details) {
        try {
            return objectMapper.writeValueAsBytes(ErrorResponse.of(status, code, details));
        } catch (RuntimeException e) {
            // Jackson 3 бросает unchecked. Провалить рендер ошибки нельзя — отдаём заглушку.
            log.error("Failed to serialize ErrorResponse", e);
            return FALLBACK_BODY;
        }
    }
}
