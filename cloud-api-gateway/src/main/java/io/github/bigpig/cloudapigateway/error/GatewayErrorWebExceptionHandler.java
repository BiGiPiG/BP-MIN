package io.github.bigpig.cloudapigateway.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Приводит все ошибки периметра к единому {@link ErrorResponse}: отказы фильтров,
 * недоступность сервисов за gateway, несопоставленные маршруты.
 * <p>
 * Порядок -2 — раньше DefaultErrorWebExceptionHandler (-1). Наличие бина типа
 * {@link ErrorWebExceptionHandler} к тому же отключает дефолтный целиком.
 */
@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ApiErrorWriter errorWriter;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        if (ex instanceof GatewayException gatewayException) {
            log.debug("Rejected on gateway: {} {}", gatewayException.getCode(), gatewayException.getMessage());
            return errorWriter.write(exchange, gatewayException.getStatus(),
                    gatewayException.getCode(), gatewayException.getMessage());
        }

        if (isConnectionFailure(ex)) {
            log.error("Upstream unreachable for {}", exchange.getRequest().getPath(), ex);
            return errorWriter.write(exchange, HttpStatus.SERVICE_UNAVAILABLE,
                    ApiErrorCode.SERVICE_UNAVAILABLE, "Service is temporarily unavailable");
        }

        if (ex instanceof ResponseStatusException statusException) {
            HttpStatus status = HttpStatus.resolve(statusException.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            // getReason() выдаёт наружу внутренности ("No static resource api/nope for
            // request 'http://...'"), поэтому наружу отдаём обобщённый текст, а подробности в лог.
            log.debug("Upstream/routing error {} for {}: {}",
                    status, exchange.getRequest().getPath(), statusException.getReason());
            return errorWriter.write(exchange, status, codeFor(status), reasonFor(status));
        }

        log.error("Unhandled error for {}", exchange.getRequest().getPath(), ex);
        return errorWriter.write(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                ApiErrorCode.INTERNAL_ERROR, "An unexpected error occurred");
    }

    private String reasonFor(HttpStatusCode status) {
        if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return "No route matches this request";
        }
        if (status.isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
            return "Service is temporarily unavailable";
        }
        if (status.isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            return "Missing or invalid access token";
        }
        return "An unexpected error occurred";
    }

    private ApiErrorCode codeFor(HttpStatusCode status) {
        if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return ApiErrorCode.NOT_FOUND;
        }
        if (status.isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
            return ApiErrorCode.SERVICE_UNAVAILABLE;
        }
        if (status.isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            return ApiErrorCode.UNAUTHORIZED;
        }
        return ApiErrorCode.INTERNAL_ERROR;
    }

    /** Netty оборачивает отказ соединения, поэтому идём по цепочке причин. */
    private boolean isConnectionFailure(Throwable ex) {
        for (Throwable current = ex; current != null; current = current.getCause()) {
            if (current instanceof IOException) {
                return true;
            }
            if (current == current.getCause()) {
                break;
            }
        }
        return false;
    }
}
