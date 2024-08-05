package com.ssafy.signal.match.service;

import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.match.domain.*;
import com.ssafy.signal.match.repository.LocationRepository;
import com.ssafy.signal.match.repository.MatchRepository;
import com.ssafy.signal.review.repository.ReviewRepository;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.notification.service.FirebaseService;
import com.ssafy.signal.review.domain.ReviewDto;
import com.ssafy.signal.review.domain.ReviewEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class MatchService {
    private final int NEAR_DISTANCE = 10;

    private final LocationRepository locationRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;
    private final FirebaseService firebaseService;

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

    public MatchResponse proposeMatch(long from_id,long to_id) throws Exception{
        Member from = Member.builder().userId(from_id).build();
        Member to = Member.builder().userId(to_id).build();

        if(!locationRepository.existsByUserId(from) || !locationRepository.existsByUserId(to)) throw new Exception("User Not Found");

        from = locationRepository.findByUserId(from).getUserId();
        to = locationRepository.findByUserId(to).getUserId();

        String body = makeMessageBody(from,to);
        firebaseService.sendMessageTo(to.getUserId(), "요청",
                body, 0);



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


        String body = makeMessageBody(from,to);


        firebaseService.sendMessageTo(to.getUserId(), makeTitle(flag),
                body, 0);

        return MatchResponse
                .builder()
                .from_id(from_id)
                .to_id(to_id)
                .name(from.getName())
                .type(from.getType())
                .comment(from.getComment())
                .build();
    }

    public MatchResponse proposeVideoCall(long from_id,long to_id) throws Exception{
        Member from = memberRepository.findById(from_id).orElseThrow();
        Member to = memberRepository.findById(to_id).orElseThrow();


        String body = makeMessageBody(from,to);
        firebaseService.sendMessageTo(
                to.getUserId(),
                "요청",
                body,
                0);


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
    public MatchResponse acceptVideoCall(long from_id, long to_id, int flag) throws Exception {
        Member from = memberRepository.findById(from_id).orElseThrow();
        Member to = memberRepository.findById(to_id).orElseThrow();

        firebaseService.sendMessageTo(to.getUserId(), makeTitle(flag),
                makeMessageBody(from,to), 0);

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

    private boolean isNearUser(MatchListResponse response,LocationDto meLocation){
        if(response.getDist() > NEAR_DISTANCE) return false;
        return meLocation.getUser_id() != response.getUser().getUserId();
    }

    private boolean isValidMember(Member me, LocationDto myLocation, MatchListResponse response){
        Member you = memberRepository
                .findById(response.getUser().getUserId())
                .orElse(Member.builder().type("None").build());

        if(you.getType().equals("None")) return false;

        if(!(response.getLocation().getMemberType() == MemberType.모두) &&
                !response.getLocation().getMemberType().name().equals(me.getType()))
            return false;
        if(!(myLocation.getMemberType() == MemberType.모두) &&
                !myLocation.getMemberType().name().equals(you.getType()))
            return false;
        return true;
    }

    public List<MatchListResponse> getMatchUser(long location_id)
    {
        LocationDto myLocation = locationRepository
                .findById(location_id)
                .orElseThrow()
                .asLocationDto();

        Member me = memberRepository
                .findById(myLocation.getUser_id())
                .orElseThrow();

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
                .filter(res->isNearUser(res,myLocation))
                .filter(res->isValidMember(me,myLocation,res))
                .toList();
    }

    private Member hideSecretInfo(Member member)
    {
        member.setPassword("");
        member.setLoginId("");
        return member;
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

    private boolean filterMatch(MatchDto matchDto, List<ReviewDto> reviews,long user_id)
    {
        long target_id = matchDto.getProposeId() == user_id ? matchDto.getAcceptId() : matchDto.getProposeId();
        for(ReviewDto reviewDto : reviews)
        {
            if(reviewDto.getUser_id() == target_id) return false;
        }
        return true;
    }

    public List<MatchDto> getMatch(long user_id)
    {
        List<ReviewDto>  reviews = Optional.ofNullable(
                reviewRepository.findAllByWriterId(Member.builder().userId(user_id).build()
                ))
                .orElse(new ArrayList<>())
                .stream()
                .map(ReviewEntity::asReviewDto)
                .toList();


        return matchRepository
                .findAllByUserId(Member.builder()
                        .userId(user_id)
                        .build())
                .stream()
                .map(MatchEntity::asMatchDto)
                .filter(matchDto -> filterMatch(matchDto,reviews,user_id))
                .toList();
    }

    private String makeTitle(int flag) { return flag == 1 ? "승낙" : "거부";}
    private String makeMessageBody(Member from,Member to) {
        return from.getUserId()+" "+to.getUserId() +" "+from.getName()+" "+from.getType()+" "+from.getComment();
    }
}
