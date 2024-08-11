package com.ssafy.signal.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostKakaoUserReq {
    String uid;
    String deviceToken;
}