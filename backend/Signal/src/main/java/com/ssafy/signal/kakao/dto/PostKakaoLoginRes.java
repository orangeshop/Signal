package com.ssafy.signal.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostKakaoLoginRes {
    private Long userId;
    private String loginId;
    private String accessToken;
    private String refreshToken;
}