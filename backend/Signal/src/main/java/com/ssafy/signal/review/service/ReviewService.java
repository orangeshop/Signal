package com.ssafy.signal.review.service;

import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.match.domain.MatchDto;
import com.ssafy.signal.match.domain.MatchEntity;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.review.domain.ReviewDto;
import com.ssafy.signal.match.repository.MatchRepository;
import com.ssafy.signal.review.domain.ReviewEntity;
import com.ssafy.signal.review.repository.ReviewRepository;
import com.ssafy.signal.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MatchRepository matchRepository;
    private final FileRepository fileRepository;
    private final MemberRepository memberRepository;

    public ReviewDto writeReview(ReviewDto reviewDto){
        Member user = Member
                .builder()
                .userId(reviewDto.getUser_id())
                .build();

        Member writer = Member
                .builder()
                .userId(reviewDto.getWriter_id())
                .build();
        if(reviewRepository.existsByUserIdAndWriterId(user,writer))
        {
            log.error("Writer already write review");
            return null;
        }

        List<MatchDto> matchUsers = Optional.ofNullable(
                        matchRepository.findAllByUserId(writer)
                ).orElse(new ArrayList<>())
                .stream()
                .map(MatchEntity::asMatchDto)
                .filter(match -> (reviewDto.getUser_id() == match.getProposeId() &&
                        reviewDto.getWriter_id() == match.getAcceptId())
                        ||
                        (reviewDto.getUser_id() == match.getAcceptId() &&
                                reviewDto.getWriter_id() == match.getProposeId())
                )
                .toList();

        if(matchUsers.isEmpty())
        {
            log.error("No match found for user " + reviewDto.getWriter_id());
            return null;
        }

        return reviewRepository
                .save(reviewDto.asReviewEntity())
                .asReviewDto();
    }

    public List<ReviewDto> getReview(long user_id)
    {
        Member user = memberRepository.findById(user_id).orElseThrow();
        FileEntity file = fileRepository.findAllByUser(user);
        String url = file == null ? "" : file.getFileUrl();

        return reviewRepository.findAllByUserId(Member
                        .builder()
                        .userId(user_id)
                        .build())
                .stream()
                .map(this::putUrlAndName)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ReviewDto putUrlAndName(ReviewEntity review)
    {
        long writer_id = review.asReviewDto().getWriter_id();
        Member writer = memberRepository.findById(writer_id).orElseThrow();
        FileEntity file = fileRepository.findAllByUser(writer);
        String url = file == null ? null : file.getFileUrl();
        return review.asReviewDto(url,writer.getName());
    }
}
