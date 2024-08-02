package com.ssafy.signal.match.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
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
    private Member writerId;

    @Column
    private int star;

    public ReviewEntity(long user_id, String content, long writer_id, int star)
    {
        this.userId = Member.builder()
                .userId(user_id)
                .build();
        this.content = content;
        this.writerId = Member.builder()
                .userId(writer_id)
                .build();
        this.star = star;
    }

    public ReviewDto asReviewDto(){
        return ReviewDto.builder()
                .review_id(review_id)
                .user_id(userId.getUserId())
                .content(content)
                .writer_id(writerId.getUserId())
                .star(star)
                .build();
    }

    public ReviewDto asReviewDto(String url){
        return ReviewDto.builder()
                .review_id(review_id)
                .user_id(userId.getUserId())
                .content(content)
                .writer_id(writerId.getUserId())
                .star(star)
                .url(url)
                .build();
    }
}
