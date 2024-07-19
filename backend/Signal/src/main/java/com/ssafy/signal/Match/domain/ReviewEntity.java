package com.ssafy.signal.Match.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(name="review")
@Entity
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long review_id;

    @Column(name="user_id")
    private long userId;

    @Column
    private String content;

    @Column
    private long writer_id;

    @Column
    private int star;

    public ReviewEntity(long user_id, String content, long writer_id, int star)
    {
        this.userId = user_id;
        this.content = content;
        this.writer_id = writer_id;
        this.star = star;
    }

    public ReviewDto asReviewDto(){
        return new ReviewDto(
                review_id,
                userId,
                content,
                writer_id,
                star
        );
    }
}
