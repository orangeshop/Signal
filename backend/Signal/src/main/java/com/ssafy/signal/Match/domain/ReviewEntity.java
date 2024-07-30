package com.ssafy.signal.Match.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(name="review")
@Entity
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long review_id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Member userId;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name="writer_id")
    private Member writer_id;

    @Column
    private int star;

    public ReviewEntity(long user_id, String content, long writer_id, int star)
    {
        this.userId = Member.builder()
                .userId(user_id)
                .build();
        this.content = content;
        this.writer_id = Member.builder()
                .userId(writer_id)
                .build();
        this.star = star;
    }

    public ReviewDto asReviewDto(){
        return new ReviewDto(
                review_id,
                userId.getUserId(),
                content,
                writer_id.getUserId(),
                star
        );
    }
}
