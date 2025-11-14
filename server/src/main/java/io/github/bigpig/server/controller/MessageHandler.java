package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.MessageDto;
import io.github.bigpig.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor  
public class MessageHandler {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void chat(@Payload MessageDto message) {
        log.info("Message received: {}", message);
        MessageDto savedMessage = messageService.save(message);
        messagingTemplate.convertAndSend("/topic/chat/" + message.chatId(), savedMessage);
    }
}
