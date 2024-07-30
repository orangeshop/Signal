package com.ssafy.signal.chat.repository;

import com.ssafy.signal.chat.domain.MessageEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<MessageEntity, Long> {
    List<MessageEntity> findByChatRoomEntity_ChatId(long chat_id);
}
