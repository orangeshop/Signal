package com.ssafy.signal.board.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="board")
public class BoardEntity extends TimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(length = 10)
    private String writer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member user;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Long reference;

    @Column
    private Long liked;

    @Column
    private Long type;

    @Builder
    public BoardEntity(Long id, String title, String content, String writer, Long reference, Long liked, Long type, Member user) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.reference = reference != null ? reference : 0L;
        this.liked = liked != null ? liked : 0L;
        this.type = type != null ? type : 0L;
        this.user = user;
    }

    // 엔티티의 상태를 변경하는 메서드 추가
    public void update(String title, String content, Long reference, Long liked, Long type) {
        this.title = title;
        this.content = content;
        this.reference = reference;
        this.liked = liked;
        this.type = type;
    }
}
