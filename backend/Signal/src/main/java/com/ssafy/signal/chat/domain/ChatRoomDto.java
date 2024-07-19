package com.ssafy.signal.chat.domain;

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
        return new ChatRoomEntity(
                from_id,
                to_id,
                last_message,
                sender_type
        );
    }
}
