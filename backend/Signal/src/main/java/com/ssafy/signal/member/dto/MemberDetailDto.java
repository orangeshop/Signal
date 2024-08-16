package com.ssafy.signal.member.dto;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.CommentDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetailDto {

    private Long userId;
    private String loginId;
    private String name;
    private List<BoardDto> boards;
    private List<CommentDto> comments;

}
