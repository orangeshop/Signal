package com.ssafy.signal.chat.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

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

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date send_at;


    public ChatRoomDto asChatRoomDto() {
        return ChatRoomDto.builder()
                .chat_id(chatId)
                .from_id(from_id.getUserId())
                .to_id(to_id.getUserId())
                .last_message(last_message)
                .sender_type(sender_type)
                .send_at(send_at)
                .build();
    }
}
