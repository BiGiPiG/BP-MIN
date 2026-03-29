package io.github.bigpig.chatservice.utils;


import io.github.bigpig.chatservice.entity.Message;

public interface MessageChecker {
    boolean checkErrors(Long userId, Message message);
}
