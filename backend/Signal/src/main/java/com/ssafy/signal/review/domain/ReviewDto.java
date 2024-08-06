package com.ssafy.signal.review.domain;

import com.ssafy.signal.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ReviewDto {
    private long review_id;
    private long user_id;
    private String content;
    private String name;

    private long writer_id;
    private int star;
    private String url;

    public ReviewEntity asReviewEntity(){
        return ReviewEntity.builder()
                .userId(Member.builder().userId(user_id).build())
                .content(content)
                .writerId(Member.builder().userId(writer_id).build())
                .star(star)
                .build();
    }
}
