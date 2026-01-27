package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;

public interface MessageChecker {
    boolean checkErrors(Long userId, Message message);
}
