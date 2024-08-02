package com.ssafy.signal.member.repository;

import com.ssafy.signal.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    void deleteByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
