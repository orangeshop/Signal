package com.ssafy.signal.member.jwt.token.dto;

import com.ssafy.signal.member.domain.Member;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(exclude = {"accessToken"})
@Builder
public class TokenInfo {
    private Boolean status;
    private String accessToken;
    private Date accessTokenExpireTime;
    private String refreshToken;
    private Date refreshTokenExpireTime;
    private Member member;
    private String tokenId;
}
