package com.ssafy.signal.chat.repository;

import com.ssafy.signal.chat.domain.ChatRoomEntity;
import com.ssafy.signal.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    @Query("SELECT c FROM ChatRoomEntity c WHERE c.to_id = :user_id OR c.from_id = :user_id")
    List<ChatRoomEntity> findChatRoomsByUserId(@Param("user_id") Member user_id);
}
