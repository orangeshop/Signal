package com.ssafy.signal.Report.Controller;

import com.ssafy.signal.Report.Dto.ReportChatRequestDto;
import com.ssafy.signal.Report.Dto.ReportRequestDto;
import com.ssafy.signal.Report.Entity.ReportBoard;
import com.ssafy.signal.Report.Entity.ReportChat;
import com.ssafy.signal.Report.Service.ReportBoardService;
import com.ssafy.signal.Report.Service.ReportChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportBoardService reportBoardService;

    @Autowired
    private ReportChatService reportChatService;

    @PostMapping("/board_and_comment")
    public ResponseEntity<ReportBoard> reportBoardOrComment(@RequestBody ReportRequestDto reportRequestDto) {
        ReportBoard reportBoard = reportBoardService.saveReportBoard(reportRequestDto);
        return ResponseEntity.ok(reportBoard);
    }

    @PostMapping("/chat")
    public ResponseEntity<ReportChat> reportChat(@RequestBody ReportChatRequestDto reportChatRequestDto) {
        ReportChat reportChat = reportChatService.saveReportChat(reportChatRequestDto);
        return ResponseEntity.ok(reportChat);
    }
}
