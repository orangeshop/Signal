package com.ssafy.signal.board.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class BoardDto {
    private Long id;
    private String writer;
    private String title;
    private String content;
    private Long reference;
    private Long liked;
    private Long type;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public BoardEntity toEntity(){
        BoardEntity boardEntity = BoardEntity.builder()
                .id(id)
                .writer(writer)
                .title(title)
                .content(content)
                .reference(reference)
                .liked(liked)
                .type(type)
                .build();
        return boardEntity;
    }
    @Builder
    public BoardDto(Long id, String title, String content, String writer, Long reference, Long liked, Long type, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.reference = reference;
        this.liked = liked;
        this.type = type;
        this.createdDate =createdDate;
        this.modifiedDate = modifiedDate;
    }
}
