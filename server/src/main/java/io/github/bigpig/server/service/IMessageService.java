package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.message.EditMessageDto;
import io.github.bigpig.server.dto.message.MessageDto;

import java.util.List;

public interface IMessageService {

    List<MessageDto> getHistory(Long chatId);

    void deleteMessage(Long messageId, Long deleterId);

    MessageDto editMessage(Long messageId, Long editorId, EditMessageDto editMessage);

    MessageDto createMessage(MessageDto messageDto);

}
