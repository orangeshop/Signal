package com.ssafy.signal.member.jwt.token;


import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.jwt.token.dto.TokenValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class TokenProviderTest {

    // 512 byte 이상의 key를 생성
    private final String secrete = "dGhpcyBpcyBteSBoaWRkZW4gand0IHNlY3JldGUga2V5LCB3aGF0IGlzIHlvdXIgand0IHNlY3JldGUga2V5Pw==";
    private final Long accessTokenValidTimeInSeconds = 3L;
    private final TokenProvider tokenProvider = new TokenProvider(secrete, accessTokenValidTimeInSeconds);

    @Test
    void createToken() {
        Member member = getMember();

        TokenInfo token = tokenProvider.createToken(member);
        log.info("access token=>{}", token.getAccessToken());
    }

    @Test
    void validateTokenValid() {
        Member member = getMember();
        TokenInfo token = tokenProvider.createToken(member);
        String accessToken = token.getAccessToken();

        TokenValidationResult tokenValidationResult = tokenProvider.validateToken(accessToken);

        Assertions.assertThat(tokenValidationResult.isValid()).isTrue();
    }

    @Test
    void validateTokenNotValid() throws InterruptedException {
        Member member = getMember();
        TokenInfo token = tokenProvider.createToken(member);
        String accessToken = token.getAccessToken();

        Thread.sleep(4000);
        TokenValidationResult tokenValidationResult = tokenProvider.validateToken(accessToken);

        Assertions.assertThat(tokenValidationResult.isValid()).isFalse();
    }

    private Member getMember() {
        return Member.builder()
                .loginId("opensw@ajou.ac.kr")
                .password("1234")
                .name("bandall")
                .build();
    }
}
