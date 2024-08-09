package com.ssafy.signal.naver.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetNaverUserRes {
    private String loginId;
    private String name;
    private String birthyear;
}
