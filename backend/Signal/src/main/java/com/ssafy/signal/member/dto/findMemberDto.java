package com.ssafy.signal.member.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class findMemberDto {
    private Long userId;
    private String profileImage;
    private String name;
}
