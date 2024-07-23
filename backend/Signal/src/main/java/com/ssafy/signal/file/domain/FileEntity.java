package com.ssafy.signal.file.domain;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.chat.domain.ChatRoomEntity;
import com.ssafy.signal.chat.domain.MessageEntity;
import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "file")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member user;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = true)  // null 허용으로 수정
    private ChatRoomEntity chatRoom;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = true)  // null 허용으로 수정
    private MessageEntity message;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type")
    private Long fileType;

    @Column(name = "file_name", nullable = false)
    private String fileName;
}
