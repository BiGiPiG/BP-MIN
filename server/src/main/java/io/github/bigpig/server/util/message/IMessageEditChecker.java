package io.github.bigpig.server.util.message;

import io.github.bigpig.server.entity.chat.Message;

public interface IMessageEditChecker {
    boolean canEdit(long editorId, Message message);
}
