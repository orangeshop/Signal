package com.ssafy.signal.Match.repository;

import com.ssafy.signal.Match.domain.LocationEntity;
import com.ssafy.signal.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity,Long> {
    boolean existsByUserId(Member userId);
    LocationEntity findByUserId(Member userId);
    void deleteAllByUserId(Member userId);
}
