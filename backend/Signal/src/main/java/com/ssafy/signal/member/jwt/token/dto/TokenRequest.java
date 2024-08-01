package com.ssafy.signal.member.jwt.token.dto;

import lombok.Data;

@Data
public class TokenRequest {
    private String refreshToken;
}
