package com.ssafy.signal.chat.repository;

import com.ssafy.signal.chat.domain.MessageEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<MessageEntity, Long> {
    List<MessageEntity> findByChatRoomEntity_ChatId(long chat_id);

    @Query("SELECT count(*) from MessageEntity m WHERE m.is_from_sender " +
            "!= :is_from_sender " +
            "AND m.is_read = false ")
    int countUnreadMessageByUserId(boolean is_from_sender);
}
