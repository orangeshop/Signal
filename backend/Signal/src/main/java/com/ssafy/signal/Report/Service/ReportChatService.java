package com.ssafy.signal.report.Service;

import com.ssafy.signal.report.Dto.ReportChatRequestDto;
import com.ssafy.signal.report.Entity.ReportChat;
import com.ssafy.signal.report.Repository.ReportChatRepository;
import com.ssafy.signal.chat.service.ChatService;
import com.ssafy.signal.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportChatService {

    @Autowired
    private ReportChatRepository reportChatRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ChatService chatService;

    public ReportChat saveReportChat(ReportChatRequestDto reportChatRequestDto) {
        ReportChat reportChat = new ReportChat();
        reportChat.setChatId(chatService.getChatRoomById(reportChatRequestDto.getChatId()));
        reportChat.setTargetMemberId(memberService.getMemberById(reportChatRequestDto.getTargetMemberId()));
        reportChat.setMemberId(memberService.getMemberById(reportChatRequestDto.getMemberId()));
        reportChat.setRepDatetime(LocalDateTime.now());
        reportChat.setComment(reportChatRequestDto.getComment());

        return reportChatRepository.save(reportChat);
    }
}
