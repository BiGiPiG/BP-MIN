package io.github.bigpig.cloudapigateway.util;

/**
 * Заголовки, которые gateway устанавливает для сервисов за ним.
 * <p>
 * Значения этих заголовков, пришедшие от клиента, всегда срезаются: сервисы
 * доверяют им безоговорочно (Spring Security за gateway нет), поэтому подставить
 * их вправе только сам gateway — и только после проверки токена.
 * <p>
 * TODO переехать в общий модуль контрактов, когда он появится: сейчас потребители
 * (chat-service, user-service) читают то же имя магической строкой.
 */
public final class InternalHeaders {

    /** Идентификатор пользователя из claim {@code userId} проверенного JWT. */
    public static final String USER_ID = "User-Id";

    private InternalHeaders() {
        throw new UnsupportedOperationException("Utility class");
    }
}
