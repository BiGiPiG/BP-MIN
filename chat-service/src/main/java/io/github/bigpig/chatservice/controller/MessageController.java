package io.github.bigpig.chatservice.controller;

import io.github.bigpig.chatservice.dto.request.DeleteMessageDto;
import io.github.bigpig.chatservice.dto.request.EditMessageDto;
import io.github.bigpig.chatservice.dto.request.ReadMessageDto;
import io.github.bigpig.chatservice.dto.response.DeletedMessageDto;
import io.github.bigpig.chatservice.dto.response.EditedMessageDto;
import io.github.bigpig.chatservice.dto.response.MarkedReadMessageDto;
import io.github.bigpig.chatservice.dto.response.MessageDto;
import io.github.bigpig.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDto message) {
        log.info("Message received: {}", message);
        MessageDto savedMessage = messageService.createMessage(message);
        String destination = String.format("/topic/chat/%s", message.chatId());
        messagingTemplate.convertAndSend(destination, savedMessage);
    }

    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(@Payload DeleteMessageDto deleteRequest,
                              SimpMessageHeaderAccessor headerAccessor) {
        log.info("Delete request for message ID: {}", deleteRequest.messageId());
        Long userId = (Long) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userId");
        messageService.deleteMessage(deleteRequest.messageId(), userId);
        DeletedMessageDto deletedMessageDto = new DeletedMessageDto(deleteRequest.messageId());
        String destination = String.format("/topic/chat/%s/deleted", deleteRequest.chatId());
        messagingTemplate.convertAndSend(destination, deletedMessageDto);
    }

    @MessageMapping("/chat.editMessage")
    public void editMessage(@Payload EditMessageDto editRequest,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info("Edit request for message ID: {}", editRequest.messageId());
        Long userId = (Long) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userId");
        MessageDto newMessage = messageService.updateMessage(editRequest.messageId(), userId, editRequest);
        EditedMessageDto editedMessageDto = new EditedMessageDto(editRequest.messageId(), newMessage.content());
        String destination = String.format("/topic/chat/%s/edited", editRequest.chatId());
        messagingTemplate.convertAndSend(destination, editedMessageDto);
    }

    @MessageMapping("/chat.readMessage")
    public void readMessage(@Payload ReadMessageDto readRequest,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info("Read request for message ID: {}", readRequest.messageId());
        Long userId = (Long) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userId");
        Long senderId = messageService.readMessage(readRequest.messageId(), userId);
        String destination = String.format("/topic/chat/%s/read/%s", readRequest.chatId(), senderId);
        log.info("Sending read request for message ID: {} to {}", readRequest.messageId(), destination);
        messagingTemplate.convertAndSend(destination,
                new MarkedReadMessageDto(readRequest.chatId(), readRequest.messageId()));
    }

}
