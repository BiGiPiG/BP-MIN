package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;

public interface IMessageDeleteChecker {
    boolean canDelete(long deleterId, Message message);
}
