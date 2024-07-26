package com.ssafy.signal.board.domain;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.ssafy.signal.member.domain.Member;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class BoardDto {
    private Long id;
    private String writer;
    private Long userId; // 새로운 필드 추가
    private String title;
    private String content;
    private Long reference;
    private Long liked;
    private Long type;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<CommentDto> comments;

    public BoardEntity toEntity(){
        BoardEntity boardEntity = BoardEntity.builder()
                .id(id)
                .writer(writer)
                .title(title)
                .content(content)
                .reference(reference)
                .liked(liked)
                .type(type)
                .user(Member.builder().userId(userId).build()) // Member 객체 참조
                .build();
        return boardEntity;
    }

    @Builder
    public BoardDto(Long id, String title, String content, String writer, Long userId, Long reference, Long liked, Long type, LocalDateTime createdDate, LocalDateTime modifiedDate, List<CommentDto> comments) {
        this.id = id;
        this.writer = writer;
        this.userId = userId; // 새로운 필드 추가
        this.title = title;
        this.content = content;
        this.reference = reference;
        this.liked = liked;
        this.type = type;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.comments = comments;
    }
}
