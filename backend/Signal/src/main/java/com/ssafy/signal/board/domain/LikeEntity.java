package com.ssafy.signal.board.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "likes")
@Getter
@Setter
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity boardEntity;

    @Column(name = "is_liked", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isLiked = false;

    // 기본 생성자
    public LikeEntity() {}

    public LikeEntity(Member user, BoardEntity boardEntity) {
        this.user = user;
        this.boardEntity = boardEntity;
        this.isLiked = true;
    }

    // liked 값을 true로 설정하는 메서드
    public void like() {
        this.isLiked = true;
    }

    // liked 값을 false로 설정하는 메서드
    public void unlike() {
        this.isLiked = false;
    }

    // isLiked에 대한 getter 메서드 추가
    public boolean isLiked() {
        return isLiked;
    }
}

