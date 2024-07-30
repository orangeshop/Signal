package com.ssafy.signal.Report.Dto;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

@Getter
@Setter
public class ReportChatRequestDto {
    private Long chatId;
    private Long targetMemberId;
    private Long MemberId;
    private String comment;
}
