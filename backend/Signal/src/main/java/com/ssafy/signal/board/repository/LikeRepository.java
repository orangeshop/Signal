package com.ssafy.signal.board.repository;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.LikeEntity;
import com.ssafy.signal.member.domain.Member;  // Member 임포트 추가
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    // Member 객체와 BoardEntity 객체를 통한 좋아요 레코드 조회
    Optional<LikeEntity> findByUserAndBoardEntity(Member user, BoardEntity boardEntity);

    // Member 객체와 BoardEntity 객체를 통한 좋아요 레코드 삭제
    void deleteByUserAndBoardEntity(Member user, BoardEntity boardEntity);
}