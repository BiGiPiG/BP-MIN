package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.message.*;
import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor  
public class MessageHandler {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDto message) {
        log.info("Message received: {}", message);
        MessageDto savedMessage = messageService.save(message);
        messagingTemplate.convertAndSend("/topic/chat/" + message.chatId(), savedMessage);
    }

    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(@Payload DeleteMessageDto deleteRequest, @AuthenticationPrincipal User user) {
        log.info("Delete request for message ID: {}", deleteRequest.messageId());

        Long currentUserId = user.getId();
        messageService.deleteMessage(deleteRequest.messageId(), currentUserId);

        DeletedMessageDto deletedMessageDto = new DeletedMessageDto(deleteRequest.messageId());
        messagingTemplate.convertAndSend("/topic/chat/" + deleteRequest.chatId() + "/deleted", deletedMessageDto);
    }

    @MessageMapping("/chat.editMessage")
    public void editMessage(@Payload EditMessageDto editRequest, @AuthenticationPrincipal User user) {
        log.info("Edit request for message ID: {}", editRequest.messageId());

        Long currentUserId = user.getId();
        MessageDto newMessage = messageService.editMessage(editRequest.messageId(), currentUserId, editRequest);

        EditedMessageDto editedMessageDto = new EditedMessageDto(editRequest.messageId(), newMessage.content());
        messagingTemplate.convertAndSend("/topic/chat/" + editRequest.chatId() + "/edited", editedMessageDto);
    }
}
