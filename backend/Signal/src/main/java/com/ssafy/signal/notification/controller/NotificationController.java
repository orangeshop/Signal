package com.ssafy.signal.notification.controller;

import com.ssafy.signal.notification.domain.TokenResponse;
import com.ssafy.signal.notification.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RestController
public class NotificationController {
    private final FirebaseService firebaseService;

    @PostMapping("/token/regist")
    public TokenResponse registerToken(@RequestParam("userId") long user_id, @RequestParam("token") String token) {
        return firebaseService.registToken(user_id, token);
    }

    @GetMapping("/noti/test")
    public void notiTest(@RequestParam("userId") long userId) throws IOException {
        firebaseService.sendMessageTo(userId,"테스트 알림","서버에서 보내는 테스트 알림입니다.",0);
    }
}
