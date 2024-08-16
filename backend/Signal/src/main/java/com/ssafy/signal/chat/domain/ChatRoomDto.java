package com.ssafy.signal.chat.domain;

import com.ssafy.signal.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
@Builder
public class ChatRoomDto {
    private long chat_id;

    private long from_id;
    private long to_id;

    private String last_message;
    private SenderType sender_type;

    private Date send_at;


    private String from_name;
    private String to_name;

    private String from_url;
    private String to_url;

    private int cnt;
    public ChatRoomEntity asChatRoomEntity() {
        return ChatRoomEntity
                .builder()
                .chatId(chat_id)
                .send_at(send_at)
                .from_id(Member.builder().userId(from_id).build())
                .to_id(Member.builder().userId(to_id).build())
                .last_message(last_message == null ? "" : last_message)
                .sender_type(sender_type == null ? SenderType.NONE : sender_type)
                .build();
    }

    public ChatRoomEntity asChatRoomEntity(String from_name, String to_name) {
        return ChatRoomEntity
                .builder()
                .chatId(chat_id)
                .send_at(send_at)
                .from_id(Member.builder().userId(from_id).build())
                .to_id(Member.builder().userId(to_id).build())
                .last_message(last_message == null ? "" : last_message)
                .sender_type(sender_type == null ? SenderType.NONE : sender_type)
                .build();
    }
}
