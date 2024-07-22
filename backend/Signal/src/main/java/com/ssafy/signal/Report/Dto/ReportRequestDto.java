package com.ssafy.signal.Report.Dto;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

@Getter
@Setter
public class ReportRequestDto {
    private Long boardId;
    private Long userId;
    private Long commentId;
    private Integer targetType;
    private String comment;
}
