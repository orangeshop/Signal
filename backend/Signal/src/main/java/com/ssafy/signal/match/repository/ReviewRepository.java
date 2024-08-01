package com.ssafy.signal.match.repository;

import com.ssafy.signal.match.domain.ReviewEntity;
import com.ssafy.signal.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity,Long> {
    List<ReviewEntity> findAllByUserId(Member user_id);
    List<ReviewEntity> findAllByWriterId(Member writer_id);
}
