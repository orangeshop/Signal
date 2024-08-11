package com.ssafy.signal.kakao.controller;

import com.ssafy.signal.kakao.service.KakaoService;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @ResponseBody
    @PostMapping("/oauth/kakao")
    public TokenInfo kakaoCallback(@RequestParam("token") String accessToken) throws Exception {
        log.info("KakaoCallback : {}", accessToken);
        return kakaoService.kakaoCallBack(accessToken);
    }
}
