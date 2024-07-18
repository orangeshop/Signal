package com.ssafy.signal.chat.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@NoArgsConstructor
@Entity
@Table(name="message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long message_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id",nullable = false)
    private ChatRoomEntity chatRoomEntity;

    @Column
    private Boolean is_from_sender;

    @Column
    private String content;

    @Column
    private Boolean is_read;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date send_at;

    public MessageEntity(Boolean isFromSender, String content,long chat_id, Boolean isRead) {
        this.is_from_sender = isFromSender;
        this.chatRoomEntity = new ChatRoomEntity();
        this.chatRoomEntity.setChatId(chat_id);
        this.content = content;
        this.is_read = isRead;
    }

    public MessageDto asMessageDto()
    {
        return new MessageDto(
                message_id,
                chatRoomEntity.getChatId(),
                is_from_sender,
                content,
                is_read,
                send_at
        );
    }
}
