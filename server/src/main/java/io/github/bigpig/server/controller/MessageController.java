package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.message.EditMessageDto;
import io.github.bigpig.server.dto.message.MessageDto;
import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDto> editMessage(
            @PathVariable Long messageId,
            @RequestBody EditMessageDto editMessage) {

        log.info("Edit message request received for message with id {}", messageId);

        Long currentUserId = getCurrentUserId();
        MessageDto editedMessage = messageService.editMessage(messageId, currentUserId, editMessage);

        return ResponseEntity.ok(editedMessage);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        log.info("Delete message request received for message with id {}", messageId);

        Long currentUserId = getCurrentUserId();
        messageService.deleteMessage(messageId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal()).getId();
    }
}
