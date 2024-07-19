package com.ssafy.signal.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


@NoArgsConstructor
@Entity
@Data
@Table(name="chat_room")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_id")
    private long chatId;

    //TODD : 유저 엔티티 추가되면 외래키 설정하기
    @ColumnDefault("0")
    private long from_id;
    @ColumnDefault("0")
    private long to_id;

    private String last_message;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("''")
    private SenderType sender_type;

    public ChatRoomEntity(long from_id, long to_id, String s, SenderType senderType) {
        this.from_id = from_id;
        this.to_id = to_id;
        this.last_message = s;
        this.sender_type = senderType;
    }

    public ChatRoomDto asChatRoomDto() {
        return new ChatRoomDto(
                chatId,
                from_id,
                to_id,
                last_message,
                sender_type
        );
    }
}
