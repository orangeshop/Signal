package com.ssafy.signal.match.controller;

import com.ssafy.signal.match.domain.*;
import com.ssafy.signal.match.service.MatchService;
import com.ssafy.signal.review.domain.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MatchController {
    private final MatchService matchService;

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

    @GetMapping("/match-test")
    public List<MatchListResponse> getMatchTest(@RequestParam("locationId") long locationId) {
        return matchService.getMatchUser(locationId);
    }

    @PostMapping("/match/propose")
    public MatchResponse proposeMatch(@RequestParam("fromId") long from_id, @RequestParam("toId") long to_id)throws Exception {
        return matchService.proposeMatch(from_id,to_id);
    }

    @PostMapping("/match/accept")
    public MatchResponse acceptMatch(@RequestParam("fromId") long from_id,@RequestParam("toId") long to_id,@RequestParam("flag") int flag) throws Exception {
        return matchService.acceptMatch(from_id, to_id,flag);
    }

    @PostMapping("/call/propose")
    public MatchResponse proposeVideoCall(@RequestParam("fromId") long from_id, @RequestParam("toId") long to_id)throws Exception {
        return matchService.proposeVideoCall(from_id,to_id);
    }

    @PostMapping("/call/accept")
    public MatchResponse acceptVideoCall(@RequestParam("fromId") long from_id,@RequestParam("toId") long to_id,@RequestParam("flag") int flag) throws Exception {
        return matchService.acceptVideoCall(from_id, to_id,flag);
    }

    @GetMapping("/match/history")
    public List<MatchDto> getMatch(@RequestParam("userId") long user_id) {
        return matchService.getMatch(user_id);
    }
}
