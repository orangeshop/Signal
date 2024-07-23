package com.ssafy.signal.board.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long boardId; // 게시판 ID를 Long 타입으로 유지
    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // CommentEntity로 변환하는 메서드
    public CommentEntity toEntity() {
        return CommentEntity.builder()
                .id(id)
                .boardEntity(new BoardEntity()) // boardId를 Long 타입으로 설정
                .writer(writer)
                .content(content)
                .build();
    }

    @Builder
    public CommentDto(Long id, Long boardId, String writer, String content, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.boardId = boardId;
        this.writer = writer;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
