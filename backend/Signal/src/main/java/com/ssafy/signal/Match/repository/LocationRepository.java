package com.ssafy.signal.Match.repository;

import com.ssafy.signal.Match.domain.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity,Long> {
}
