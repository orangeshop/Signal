package com.ssafy.signal.report.Repository;

import com.ssafy.signal.report.Entity.ReportBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportBoardRepository extends JpaRepository<ReportBoard, Long> {
}
