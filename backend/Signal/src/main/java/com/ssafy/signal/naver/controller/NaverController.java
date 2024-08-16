package com.ssafy.signal.naver.controller;


import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.naver.service.NaverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NaverController {

    private final NaverService naverService;

    @ResponseBody
    @PostMapping("/oauth/naver")
    public TokenInfo naverCallback(@RequestParam("token") String accessToken) throws Exception {
        log.info("naverCallback : {}", accessToken);
        return naverService.naverCallBack(accessToken);
    }
}
