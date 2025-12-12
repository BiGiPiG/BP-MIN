package io.github.bigpig.server.dto.chat;

import io.github.bigpig.server.entity.chat.ChatType;
import java.util.List;

public record CreateChatRequestDto(
    ChatType type,
    String title,
    List<String> participants
) {}
