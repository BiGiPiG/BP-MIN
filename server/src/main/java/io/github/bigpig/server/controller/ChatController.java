package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.chat.ChatDto;
import io.github.bigpig.server.dto.chat.CreateChatRequestDto;
import io.github.bigpig.server.dto.message.MessageDto;
import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.service.ChatService;
import io.github.bigpig.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<ChatDto>> getChats(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(chatService.findChatsByUser(userDetails)
                .stream().map(chatService::getChatDto).toList());
    }

    @PostMapping("/create")
    public ResponseEntity<ChatDto> createChat(@RequestBody CreateChatRequestDto requestDto) {
        log.info("Create chat: {}", requestDto);
        ChatDto newChatDto = chatService.getChatDto(chatService.createChat(requestDto));
        return ResponseEntity.ok(newChatDto);
    }

    @GetMapping("/history/{chatId}")
    public ResponseEntity<List<MessageDto>> getHistory(@PathVariable Long chatId) {
        return ResponseEntity.ok(messageService.getHistory(chatId));
    }
}
