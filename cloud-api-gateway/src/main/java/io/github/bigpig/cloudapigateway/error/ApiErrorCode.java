package io.github.bigpig.cloudapigateway.error;

/**
 * Машиночитаемые коды ошибок, которые порождает сам периметр.
 * Коды бизнес-ошибок живут в сервисах — см. api/openapi.yaml.
 */
public enum ApiErrorCode {

    /** Токен отсутствует, просрочен или не прошёл проверку подписи. */
    UNAUTHORIZED,

    /** Подпись сошлась, но по токену нельзя установить пользователя. */
    TOKEN_INVALID,

    /** Ни один маршрут не подошёл. */
    NOT_FOUND,

    /** Сервис за gateway недоступен. */
    SERVICE_UNAVAILABLE,

    /** Всё остальное. */
    INTERNAL_ERROR
}
