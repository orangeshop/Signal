package com.ssafy.signal.match.repository;

import com.ssafy.signal.match.domain.MatchEntity;
import com.ssafy.signal.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntity,Long> {
    @Query("SELECT c FROM MatchEntity c WHERE c.proposeId = :user_id OR c.acceptId = :user_id")
    List<MatchEntity> findAllByUserId(Member user_id);
}
