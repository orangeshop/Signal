package com.ssafy.signal.Match.repository;

import com.ssafy.signal.Match.domain.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity,Long> {
    List<ReviewEntity> findAllByUserId(long user_id);
}
