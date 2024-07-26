package com.ssafy.signal.Match.controller;

import com.ssafy.signal.Match.domain.LocationDto;
import com.ssafy.signal.Match.domain.MatchListResponse;
import com.ssafy.signal.Match.domain.MatchResponse;
import com.ssafy.signal.Match.domain.ReviewDto;
import com.ssafy.signal.Match.service.MatchService;
import com.ssafy.signal.member.domain.Member;
import lombok.RequiredArgsConstructor;
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
    @DeleteMapping("/location/user")
    public void deleteLocations(@RequestParam("userId") long user_id) {
        matchService.deleteLocationByUserId(user_id);
    }

    @GetMapping("/match")
    public List<Member> getNearUser(@RequestParam("locationId") long locationId) {
        return matchService.getNearUser(locationId);
    }

    @GetMapping("/match-test")
    public List<MatchListResponse> getMatchTest(@RequestParam("locationId") long locationId) {
        return matchService.getMatchUser(locationId);
    }

    @PostMapping("/token/regist")
    public String registerToken(@RequestParam("userId") long user_id,@RequestParam("token") String token) {
        return matchService.registToken(user_id, token);
    }

    @PostMapping("/match/propose")
    public MatchResponse proposeMatch(@RequestParam("fromId") long from_id, @RequestParam("toId") long to_id)throws Exception {
        return matchService.proposeMatch(from_id,to_id);
    }

    @PostMapping("/match/accept")
    public MatchResponse acceptMatch(@RequestParam("fromId") long from_id,@RequestParam("toId") long to_id) throws Exception {
        return matchService.acceptMatch(from_id, to_id);
    }
}
