package io.github.bigpig.chatservice.controller;

import io.github.bigpig.chatservice.dto.request.CreateChatRequest;
import io.github.bigpig.chatservice.dto.response.ChatDto;
import io.github.bigpig.chatservice.dto.response.MessageDto;
import io.github.bigpig.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatDto>> getChats(@RequestHeader(name = "User-Id") long userId) {
        log.info("Received request to find chats for user {}", userId);
        return ResponseEntity.ok().body(chatService.getChats(userId));
    }

    @PostMapping
    public ResponseEntity<ChatDto> createChat(@RequestBody CreateChatRequest requestDto) {
        log.info("Create chat: {}", requestDto);
        ChatDto newChat = chatService.createChat(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newChat);
    }

    @GetMapping("/history/{chatId}")
    public ResponseEntity<List<MessageDto>> getHistory(@PathVariable long chatId,
                                                       @RequestHeader("User-Id") long userId) {
        List<MessageDto> history = chatService.getHistory(chatId, userId);
        return ResponseEntity.ok().body(history);
    }
}
