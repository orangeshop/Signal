package com.ssafy.signal.report.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDto {
    private Long boardId;
    private Long userId;
    private Long commentId;
    private Integer targetType;
    private String comment;
}
