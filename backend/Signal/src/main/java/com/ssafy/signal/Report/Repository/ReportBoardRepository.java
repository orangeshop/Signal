package com.ssafy.signal.Report.Repository;

import com.ssafy.signal.Report.Entity.ReportBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportBoardRepository extends JpaRepository<ReportBoard, Long> {
}
