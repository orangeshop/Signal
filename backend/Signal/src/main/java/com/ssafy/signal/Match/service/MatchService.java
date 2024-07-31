package com.ssafy.signal.Match.service;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.ssafy.signal.Match.domain.*;
import com.ssafy.signal.Match.repository.LocationRepository;
import com.ssafy.signal.Match.repository.MatchRepository;
import com.ssafy.signal.Match.repository.ReviewRepository;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.notification.service.FirebaseService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MatchService {
    private static final int EARTH_RADIUS = 6371;
    private final int NEAR_DISTANCE = 10;

    private final LocationRepository locationRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;

    private final FirebaseService firebaseService;

    Map<Long,String> userTokens = new ConcurrentHashMap<>();

    public ReviewDto writeReview(ReviewDto reviewDto) {
        return reviewRepository
                .save(reviewDto.asReviewEntity())
                .asReviewDto();
    }

    public List<ReviewDto> getReview(long user_id)
    {
        return reviewRepository.findAllByUserId(Member
                        .builder()
                        .userId(user_id)
                        .build())
                .stream()
                .map(ReviewEntity::asReviewDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public LocationDto saveLocation(LocationDto locationDto) {
        Member user = Member
                .builder()
                .userId(locationDto.getUser_id())
                .build();

        if(locationRepository.existsByUserId(user))
            return locationRepository.findByUserId(user).asLocationDto();

        return locationRepository
                .save(locationDto.asLocationEntity())
                .asLocationDto();
    }

    public TokenResponse registToken(long user_id, String token) {
        userTokens.put(user_id,token);
        System.out.println("Now " + user_id +" "+token);
        System.out.println();
        System.out.println();
        for(long user : userTokens.keySet())
        {
            System.out.println(user + " regist " + userTokens.get(user));
        }
        return TokenResponse.builder().user_id(user_id).token(token).build();
    }

    public MatchResponse proposeMatch(long from_id,long to_id) throws Exception{
        Member from = Member.builder().userId(from_id).build();
        Member to = Member.builder().userId(to_id).build();

        if(!locationRepository.existsByUserId(from) || !locationRepository.existsByUserId(to)) throw new Exception("User Not Found");

        from = locationRepository.findByUserId(from).getUserId();
        to = locationRepository.findByUserId(to).getUserId();

        String token = userTokens.get(to.getUserId());

        if(token != null)
            firebaseService.sendMessageTo(token,"요청",from.getUserId()+" "+to.getUserId() +" "+from.getName()+" "+from.getType()+" "+from.getComment());


        return MatchResponse
                .builder()
                .from_id(from_id)
                .to_id(to_id)
                .name(from.getName())
                .type(from.getType())
                .comment(from.getComment())
                .build();
    }

    @Transactional
    public MatchResponse acceptMatch(long from_id,long to_id, int flag) throws Exception{
        Member from = Member.builder().userId(from_id).build();
        Member to = Member.builder().userId(to_id).build();
        System.out.println(from_id + " send to " + to_id);
        if(!locationRepository.existsByUserId(from) || !locationRepository.existsByUserId(to)) throw new Exception("User Not Found");


        LocationDto fromLocation = locationRepository.findByUserId(from).asLocationDto();
        LocationDto toLocation = locationRepository.findByUserId(to).asLocationDto();

        from = locationRepository.findByUserId(from).getUserId();
        to = locationRepository.findByUserId(to).getUserId();

        if(flag == 1)
        {
            locationRepository.deleteById(fromLocation.getLocation_id());
            locationRepository.deleteById(toLocation.getLocation_id());
            saveMatch(from_id,to_id);
        }

        String token = userTokens.get(to.getUserId());


        if(token != null && flag == 1)
            firebaseService.sendMessageTo(token,"승낙",from.getUserId()+" "+to.getUserId() +" "+from.getName()+" "+from.getType()+" "+from.getComment());
        if(token != null && flag == 0)
            firebaseService.sendMessageTo(token,"거부",from.getUserId()+" "+to.getUserId() +" "+from.getName()+" "+from.getType()+" "+from.getComment());

        return MatchResponse
                .builder()
                .from_id(from_id)
                .to_id(to_id)
                .name(from.getName())
                .type(from.getType())
                .comment(from.getComment())
                .build();
    }

    public void deleteLocation(long locationId) {
        locationRepository.deleteById(locationId);
    }

    @Transactional
    public void deleteLocationByUserId(long user_id) {
        locationRepository.deleteAllByUserId(Member.builder().userId(user_id).build());
    }

    public List<Member> getNearUser(long location_id)
    {
        LocationDto myLocation = locationRepository
                .findById(location_id)
                .orElseThrow()
                .asLocationDto();

        List<Long> users = locationRepository
                .findAll()
                .stream()
                .map(LocationEntity::asLocationDto)
                .filter(location->
                        location.getLocation_id() != location_id &&
                                getDistance(myLocation.getLatitude(),
                                        myLocation.getLongitude(),
                                        location.getLatitude(),
                                        location.getLongitude()) <= NEAR_DISTANCE)
                .map(LocationDto::getUser_id)
                .toList();

        return memberRepository
                .findAllById(users)
                .stream()
                .map(this::hideSecretInfo)
                .toList();
    }

    public List<MatchListResponse> getMatchUser(long location_id)
    {
        LocationDto myLocation = locationRepository
                .findById(location_id)
                .orElseThrow()
                .asLocationDto();

        List<LocationDto> locations = locationRepository
                .findAll()
                .stream()
                .map(LocationEntity::asLocationDto)
                .toList();

        Map<Long, Member> userMap = memberRepository.findAllById(
                        locations.stream()
                                .map(LocationDto::getUser_id)
                                .toList()
                ).stream()
                .collect(Collectors.toMap(Member::getUserId, this::hideSecretInfo));

        return locations.stream()
                .map(location -> MatchListResponse.asMatchResponse(
                        userMap.get(location.getUser_id()),
                        location,
                        myLocation.getLatitude(),
                        myLocation.getLongitude()
                ))
                .filter(matchListResponse ->
                        matchListResponse.getDist() <= NEAR_DISTANCE &&
                        matchListResponse.getLocation().getLocation_id() != location_id)
                .toList();
    }

    private Member hideSecretInfo(Member member)
    {
        return Member
                .builder()
                .userId(member.getUserId())
                .type(member.getType())
                .name(member.getName())
                .comment(member.getComment())
                .build();
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2)* Math.sin(dLat/2)+ Math.cos(Math.toRadians(lat1))* Math.cos(Math.toRadians(lat2))* Math.sin(dLon/2)* Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;
    }

    private MatchDto saveMatch(long from_id,long to_id)
    {
        return matchRepository.save(MatchDto.builder()
                        .proposeId(to_id)
                        .acceptId(from_id)
                        .build()
                        .asMatchEntity()
                )
                .asMatchDto();
    }

    public List<MatchDto> getMatch(long user_id)
    {
        return matchRepository
                .findAllByUserId(Member.builder()
                        .userId(user_id)
                        .build())
                .stream()
                .map(MatchEntity::asMatchDto)
                .toList();
    }
}
