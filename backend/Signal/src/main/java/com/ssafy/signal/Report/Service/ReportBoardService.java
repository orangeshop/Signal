package com.ssafy.signal.report.Service;

import com.ssafy.signal.report.Dto.ReportRequestDto;
import com.ssafy.signal.report.Entity.ReportBoard;
import com.ssafy.signal.report.Repository.ReportBoardRepository;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.service.BoardService;
import com.ssafy.signal.board.service.CommentService;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportBoardService {

    @Autowired
    private ReportBoardRepository reportBoardRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;




    public ReportBoard saveReportBoard(ReportRequestDto reportRequestDto) {
        Member user = memberService.getMemberById(reportRequestDto.getUserId());
        BoardEntity board = null;
        CommentEntity comment = null;

        if (reportRequestDto.getBoardId() != null) {
            board = boardService.getBoardById(reportRequestDto.getBoardId());
        }
        if (reportRequestDto.getCommentId() != null) {
            comment = commentService.getCommentById(reportRequestDto.getCommentId());
        }

        ReportBoard reportBoard = new ReportBoard();
        reportBoard.setBoardId(board);
        reportBoard.setUserId(user);
        reportBoard.setCommentId(comment);
        reportBoard.setTargetType(reportRequestDto.getTargetType());
        reportBoard.setRepDatetime(LocalDateTime.now());
        reportBoard.setComment(reportRequestDto.getComment());

        return reportBoardRepository.save(reportBoard);
    }


}
