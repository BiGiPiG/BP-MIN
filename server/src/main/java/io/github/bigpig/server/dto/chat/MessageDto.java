package io.github.bigpig.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record MessageDto(
        Long id,                       // message ID
        Long chatId,                   // chat ID
        String senderId,               // sender ID
        String content,                // message content

        @JsonFormat(pattern = "HH:mm")
        LocalDateTime shortSentAt,     // send time

        String fullSentAt              // send date with time
) {}
