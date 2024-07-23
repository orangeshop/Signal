package com.ssafy.signal.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "comment")
public class CommentEntity extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false) // 게시판과의 관계 설정
    private BoardEntity boardEntity;

    @Column(nullable = false)
    private String writer;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
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

    // boardId를 반환하는 메서드 추가
    public Long getBoardId() {
        return this.boardEntity != null ? this.boardEntity.getId() : null;
    }
}
