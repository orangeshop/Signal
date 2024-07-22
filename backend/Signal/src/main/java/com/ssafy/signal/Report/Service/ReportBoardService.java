package com.ssafy.signal.Report.Service;

import com.ssafy.signal.Report.Dto.ReportRequestDto;
import com.ssafy.signal.Report.Entity.ReportBoard;
import com.ssafy.signal.Report.Repository.ReportBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportBoardService {

    @Autowired
    private ReportBoardRepository reportBoardRepository;


    public ReportBoard saveReportBoard(ReportRequestDto reportRequestDto) {
        ReportBoard reportBoard = new ReportBoard();
        reportBoard.setBoardId(reportRequestDto.getBoardId());
        reportBoard.setUserId(reportRequestDto.getUserId());
        reportBoard.setCommentId(reportRequestDto.getCommentId());
        reportBoard.setTargetType(reportRequestDto.getTargetType());
        reportBoard.setRepDatetime(LocalDateTime.now());
        reportBoard.setComment(reportRequestDto.getComment());

        return reportBoardRepository.save(reportBoard);
    }


}
