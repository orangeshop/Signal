package com.ssafy.signal.member.dto;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.CommentDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MemberDetailDto {

    private Long userId;
    private String loginId;
    private String name;
    private List<BoardDto.SimpleBoardDto> boards;
    private List<CommentDto.SimpleCommentDto> comments;

    public MemberDetailDto(Long userId, String loginId, String name, List<BoardDto.SimpleBoardDto> boards, List<CommentDto.SimpleCommentDto> comments) {
        this.userId = userId;
        this.loginId = loginId;
        this.name = name;
        this.boards = boards;
        this.comments = comments;
    }
}
