package com.ssafy.signal.file.domain;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.chat.domain.ChatRoomEntity;
import com.ssafy.signal.chat.domain.MessageEntity;
import com.ssafy.signal.member.domain.Member;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private Long id;
    private Long boardId;
    private Long userId;
    private Long chatRoomId;
    private Long messageId;
    private String fileUrl;
    private Long fileType;
    private String fileName;

    // FileEntity를 FileDto로 변환하는 생성자
    public FileDto(FileEntity fileEntity) {
        this.id = fileEntity.getId();
        this.boardId = fileEntity.getBoard() != null ? fileEntity.getBoard().getId() : null;
        this.userId = fileEntity.getUser() != null ? fileEntity.getUser().getUserId() : null;
        this.chatRoomId = fileEntity.getChatRoom() != null ? fileEntity.getChatRoom().getChatId() : null;
        this.messageId = fileEntity.getMessage() != null && fileEntity.getMessage().asMessageDto() != null ? fileEntity.getMessage().asMessageDto().getMessage_id() : null;
        this.fileUrl = fileEntity.getFileUrl();
        this.fileType = fileEntity.getFileType();
        this.fileName = fileEntity.getFileName();
    }

    // FileDto를 FileEntity로 변환하는 메서드
    public FileEntity toEntity(BoardEntity board, Member user, ChatRoomEntity chatRoom, MessageEntity message) {
        return new FileEntity(
                this.id,
                board,
                user,
                chatRoom,
                message,
                this.fileUrl,
                this.fileType,
                this.fileName
        );
    }
}