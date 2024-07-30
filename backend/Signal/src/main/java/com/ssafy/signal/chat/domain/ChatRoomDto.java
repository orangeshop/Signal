package com.ssafy.signal.chat.domain;

import com.ssafy.signal.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChatRoomDto {
    private long chat_id;

    private long from_id;
    private long to_id;

    private String last_message;
    private SenderType sender_type;

    public ChatRoomEntity asChatRoomEntity() {
        return ChatRoomEntity
                .builder()
                .chatId(chat_id)
                .from_id(Member.builder().userId(from_id).build())
                .to_id(Member.builder().userId(to_id).build())
                .last_message(last_message == null ? "" : last_message)
                .sender_type(sender_type == null ? SenderType.NONE : sender_type)
                .build();
    }
}
