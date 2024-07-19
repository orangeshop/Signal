package com.ssafy.signal.Match.controller;

import com.ssafy.signal.Match.domain.LocationDto;
import com.ssafy.signal.Match.domain.ReviewDto;
import com.ssafy.signal.Match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/review")
    public ReviewDto review(@RequestBody ReviewDto reviewDto) {
        return matchService.writeReview(reviewDto);
    }

    @GetMapping("/review")
    public List<ReviewDto> getReviews(@RequestParam("userId") long userId) {
        return matchService.getReview(userId);
    }

    @PostMapping("/location")
    public LocationDto location(@RequestBody LocationDto locationDto) {
        return matchService.saveLocation(locationDto);
    }

    @DeleteMapping("/location")
    public void deleteLocation(@RequestParam("locationId") long locationId) {
        matchService.deleteLocation(locationId);
    }
}
