package com.ssafy.signal.chat.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Builder
@AllArgsConstructor
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
    @OneToOne()
    @JoinColumn(name="from_id")
    private Member from_id;

    @OneToOne()
    @JoinColumn(name="to_id")
    private Member to_id;

    @ColumnDefault("''")
    private String last_message;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NONE'")
    private SenderType sender_type;

    public ChatRoomEntity(long from_id, long to_id, String s, SenderType senderType) {
        this.from_id = new Member();
        this.from_id.setUserId(from_id);

        this.to_id = new Member();
        this.to_id.setUserId(to_id);

        this.last_message = s;
        this.sender_type = senderType;
    }

    public ChatRoomDto asChatRoomDto() {
        return new ChatRoomDto(
                chatId,
                from_id.getUserId(),
                to_id.getUserId(),
                last_message,
                sender_type
        );
    }
}
