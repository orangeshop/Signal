package com.ssafy.signal.member.dto;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.CommentDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MemberLoginDto {
    @NotNull
    private String loginId;

    @NotNull
    private String password;

}
