package com.ssafy.signal.match.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReviewDto {
    private long review_id;
    private long user_id;
    private String content;

    private long writer_id;
    private int star;
    public ReviewEntity asReviewEntity(){
        return new ReviewEntity(
                user_id,
                content,
                writer_id,
                star
        );
    }
}
