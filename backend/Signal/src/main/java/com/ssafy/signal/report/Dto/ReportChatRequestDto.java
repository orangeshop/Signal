package com.ssafy.signal.report.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportChatRequestDto {
    private Long chatId;
    private Long targetMemberId;
    private Long MemberId;
    private String comment;
}
