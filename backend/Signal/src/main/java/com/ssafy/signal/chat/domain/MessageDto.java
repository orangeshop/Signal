package com.ssafy.signal.chat.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDto {
    private long message_id;

    private long chat_id;

    private Boolean is_from_sender;
    private String content;
    private Boolean is_read;
    private Date send_at;

    public MessageEntity asMessageEntity() {
        return new MessageEntity(
                is_from_sender,
                content,
                chat_id,
                is_read
        );
    }
}
