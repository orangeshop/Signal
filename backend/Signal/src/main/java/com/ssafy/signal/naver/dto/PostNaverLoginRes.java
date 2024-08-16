package com.ssafy.signal.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostNaverLoginRes {
    private Long userId;
    private String loginId;
    private String accessToken;
    private String refreshToken;
}
