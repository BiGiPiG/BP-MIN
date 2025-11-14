package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.ChatDto;
import io.github.bigpig.server.dto.CreateChatRequestDto;
import io.github.bigpig.server.dto.MessageDto;
import io.github.bigpig.server.entity.User;
import io.github.bigpig.server.service.ChatService;
import io.github.bigpig.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        Long userId = userDetails.getId();
        return new ResponseEntity<>(
                chatService.findChatsByUserId(userId).stream().map(chatService::getChatDto).toList(),
                HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ChatDto> createChat(@RequestBody CreateChatRequestDto requestDto) {
        log.info("Create chat: {}", requestDto);
        ChatDto newChatDto = chatService.getChatDto(chatService.createChat(requestDto));
        return new ResponseEntity<>(newChatDto, HttpStatus.CREATED);
    }

    @GetMapping("/history/{chatId}")
    public ResponseEntity<List<MessageDto>> getHistory(@PathVariable Long chatId) {
        return new ResponseEntity<>(messageService.getHistory(chatId), HttpStatus.OK);
    }
}
