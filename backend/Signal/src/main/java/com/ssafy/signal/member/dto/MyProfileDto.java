package com.ssafy.signal.member.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MyProfileDto {
    private Long userId;
    private String loginId;
    private String type;
    private String name;
    private String profileImage;
    private String comment;
    private int score;
}