package com.ssafy.signal.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostNaverUserReq {
    String uid;
    String deviceToken;
}
