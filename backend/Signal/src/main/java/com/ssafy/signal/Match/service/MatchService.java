package com.ssafy.signal.Match.service;

import com.ssafy.signal.Match.domain.LocationDto;
import com.ssafy.signal.Match.domain.LocationEntity;
import com.ssafy.signal.Match.domain.ReviewDto;
import com.ssafy.signal.Match.domain.ReviewEntity;
import com.ssafy.signal.Match.repository.LocationRepository;
import com.ssafy.signal.Match.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final LocationRepository locationRepository;
    private final ReviewRepository reviewRepository;

    public ReviewDto writeReview(ReviewDto reviewDto) {
        return reviewRepository
                .save(reviewDto.asReviewEntity())
                .asReviewDto();
    }

    public List<ReviewDto> getReview(long user_id)
    {
        return reviewRepository.findAllByUserId(user_id)
                .stream()
                .map(ReviewEntity::asReviewDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public LocationDto saveLocation(LocationDto locationDto) {
        return locationRepository
                .save(locationDto.asLocationEntity())
                .asLocationDto();
    }

    public void deleteLocation(long locationId) {
        locationRepository.deleteById(locationId);
    }
}
