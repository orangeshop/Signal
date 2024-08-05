package com.ssafy.signal.board.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "comment")
public class CommentEntity extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity boardEntity;

    @Column(nullable = false)
    private String writer;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Member userId;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    public CommentEntity(Long id, BoardEntity boardEntity, String writer, String content) {
        this.id = id;
        this.boardEntity = boardEntity;
        this.writer = writer;
        this.content = content;
    }

    // JPA 엔티티의 수동 getter 메서드 (TimeEntity로부터 상속된 필드)
    @Override
    public LocalDateTime getCreatedDate() {
        return super.getCreatedDate();
    }

    @Override
    public LocalDateTime getModifiedDate() {
        return super.getModifiedDate();
    }

    // CommentDto로부터 업데이트하는 메서드
    public void updateFromDto(CommentDto commentDto) {
        if (commentDto.getContent() != null) {
            this.content = commentDto.getContent();
        }
    }



    public CommentDto asCommentDto()
    {
        return CommentDto.builder()
                .id(id)
                .boardId(boardEntity.getId())
                .userId(userId.getUserId())
                .writer(writer)
                .content(content)
                .createdDate(getCreatedDate())
                .modifiedDate(getModifiedDate())
                .build();
    }

    public CommentDto asCommentDto(String url)
    {
        return CommentDto.builder()
                .id(id)
                .url(url)
                .boardId(boardEntity.getId())
                .userId(userId.getUserId())
                .writer(writer)
                .content(content)
                .createdDate(getCreatedDate())
                .modifiedDate(getModifiedDate())
                .build();
    }
}
