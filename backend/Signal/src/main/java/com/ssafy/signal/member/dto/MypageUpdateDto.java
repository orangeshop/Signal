package com.ssafy.signal.member.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MypageUpdateDto {

    private Long userId;
    private String loginId;
    private String password;
    private String type;
    private String name;
    private String comment;
}
