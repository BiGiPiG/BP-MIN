# BP-MIN — real-time контракт (STOMP over WebSocket)

Дополняет [`openapi.yaml`](./openapi.yaml). OpenAPI описывает только REST,
а основная часть работы мессенджера идёт через STOMP — поэтому эта половина
контракта живёт здесь.

## Подключение

| | |
|---|---|
| Endpoint | `ws://localhost:9002/handshake` |
| Префикс команд клиента | `/bp-min` |
| Префикс подписок | `/topic` |
| Аутентификация | STOMP-заголовок `Authorization: Bearer <accessToken>` в кадре `CONNECT` |

Токен тот же, что и для REST (`/api/auth/signin`). При отсутствии или
невалидности заголовка соединение отклоняется на этапе `CONNECT`.

> **Расхождение с REST.** WebSocket сейчас идёт напрямую в chat-service на порт
> `9002`, минуя api-gateway (`8080`). У внешнего API получаются два разных
> origin. Свести к одному — отдельная задача.

## Клиент → сервер

Отправляются как `SEND` на destination ниже.

| Destination | Payload | Примечание |
|---|---|---|
| `/bp-min/chat.sendMessage` | `{ chatId, senderId, content }` | `senderId` сейчас берётся **из payload**, а не из сессии |
| `/bp-min/chat.editMessage` | `{ messageId, chatId, newContent }` | автор определяется по сессии |
| `/bp-min/chat.deleteMessage` | `{ messageId, chatId }` | автор определяется по сессии |
| `/bp-min/chat.readMessage` | `{ messageId, chatId }` | читатель определяется по сессии |

> **Известная проблема.** `chat.sendMessage` доверяет `senderId` из тела кадра,
> тогда как остальные три команды берут пользователя из атрибутов сессии. Это
> позволяет участнику чата отправить сообщение от чужого имени. Исправляется
> переходом на сессию — контракт при этом теряет поле `senderId` в payload.

## Сервер → клиент

| Destination | Payload |
|---|---|
| `/topic/chat/{chatId}` | `Message` (та же схема, что в `openapi.yaml`) |
| `/topic/chat/{chatId}/edited` | `{ messageId, newContent }` |
| `/topic/chat/{chatId}/deleted` | `{ messageId }` |
| `/topic/chat/{chatId}/read/{senderId}` | `{ chatId, messageId }` |
| `/topic/chat/{chatId}/status` | `{ userId, status }`, где `status` — `online` \| `offline` |
| `/topic/user/{userId}/chats` | `Chat` — приходит, когда пользователя добавили в новый чат |

Подписка `/topic/chat/{chatId}/read/{senderId}` адресована **автору** сообщения:
уведомление о прочтении приходит только ему.

## Ограничения текущей реализации

- Брокер in-memory (`enableSimpleBroker`), поэтому chat-service работает только
  в одном экземпляре: клиенты на разных нодах не увидят сообщений друг друга.
- Нет подтверждений доставки и переотправки — кадр, потерянный при разрыве
  соединения, не восстанавливается.
- Нет ack/receipt на командах клиента: об ошибке обработки клиент не узнаёт.
