package com.ssafy.signal.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "comment")
public class CommentEntity extends TimeEntity {

    public void updateFromDto(CommentDto commentDto) {
        // 필요한 필드만 업데이트
        if (commentDto.getContent() != null) {
            this.content = commentDto.getContent();
        }
        // 다른 필드도 필요에 따라 업데이트
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @Column(nullable = false)
    private String writer;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public CommentEntity(Long id, BoardEntity board, String writer, String content) {
        this.id = id;
        this.board = board;
        this.writer = writer;
        this.content = content;
    }

    // 수동으로 getter 메서드 추가
    public LocalDateTime getCreatedDate() {
        return super.getCreatedDate();
    }

    public LocalDateTime getModifiedDate() {
        return super.getModifiedDate();
    }
}
