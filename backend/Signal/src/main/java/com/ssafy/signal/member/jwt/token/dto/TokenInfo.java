package com.ssafy.signal.member.jwt.token.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(exclude = {"accessToken"})
@Builder
public class TokenInfo {
    private String accessToken;
    private Date accessTokenExpireTime;
    private String ownerLoginId;
    private String tokenId;
}
