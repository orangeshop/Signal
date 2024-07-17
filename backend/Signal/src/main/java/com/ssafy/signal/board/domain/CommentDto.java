package com.ssafy.signal.board.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long boardId;
    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public CommentEntity toEntity(BoardEntity boardEntity) {
        return CommentEntity.builder()
                .id(id)
                .board(boardEntity)
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

