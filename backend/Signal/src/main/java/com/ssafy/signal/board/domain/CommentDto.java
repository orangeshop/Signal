package com.ssafy.signal.board.domain;

import com.ssafy.signal.member.domain.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long boardId; // 게시판 ID를 Long 타입으로 유지
    private Long userId;
    private String url;
    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // CommentEntity로 변환하는 메서드
    public CommentEntity toEntity() {
        return CommentEntity.builder()
                .id(id)
                .boardEntity(BoardEntity.builder().id(boardId).build()) // boardId를 Long 타입으로 설정
                .userId(Member.builder().userId(userId).build())
                .writer(writer)
                .content(content)
                .build();
    }

    @Builder
    public CommentDto(Long id, Long boardId, String writer, String url, String content, Long userId, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.url = url;
        this.writer = writer;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
