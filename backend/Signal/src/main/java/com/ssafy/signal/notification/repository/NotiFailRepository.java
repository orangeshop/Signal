package com.ssafy.signal.notification.repository;


import com.ssafy.signal.notification.domain.NotiFailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiFailRepository extends JpaRepository<NotiFailEntity, Long> {
}
