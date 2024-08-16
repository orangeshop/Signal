package com.ssafy.signal.member.jwt.token.dto;

import com.ssafy.signal.member.jwt.token.TokenStatus;
import com.ssafy.signal.member.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import lombok.*;

@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
public class TokenValidationResult {
    private TokenStatus tokenStatus;
    private TokenType tokenType;
    private String tokenId;
    private Claims claims;

    public String getLoginId() {
        if (claims == null) {
            throw new IllegalStateException("Claim value is null");
        }
        return claims.getSubject();
    }

    public boolean isValid() {
        return TokenStatus.TOKEN_VALID == this.tokenStatus;
    }
}
