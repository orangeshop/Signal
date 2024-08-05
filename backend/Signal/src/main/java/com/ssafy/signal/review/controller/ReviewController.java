package com.ssafy.signal.review.controller;

import com.ssafy.signal.review.domain.ReviewDto;
import com.ssafy.signal.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/review")
    public ReviewDto review(@RequestBody ReviewDto reviewDto) {
        return reviewService.writeReview(reviewDto);
    }

    @GetMapping("/review")
    public List<ReviewDto> getReviews(@RequestParam("userId") long userId) {
        return reviewService.getReview(userId);
    }
}
