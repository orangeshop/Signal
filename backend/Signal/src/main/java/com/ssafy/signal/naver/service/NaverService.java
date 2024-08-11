package com.ssafy.signal.naver.service;

import com.google.gson.Gson;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.LoginDto;
import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.naver.dto.GetNaverUserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenInfo naverCallBack(String accessToken) throws Exception {
        GetNaverUserRes getNaverUserRes = getUserInfo(accessToken);
        String loginId = getNaverUserRes.getLoginId();
        String name = getNaverUserRes.getName();
        int birthyear = Integer.valueOf(getNaverUserRes.getBirthyear());
        Optional<Member> findMember = memberRepository.findByLoginId(loginId);

        if (!findMember.isPresent()) {
            Member member = Member.builder()
                    .loginId(loginId)
                    .name(name)
                    .password(passwordEncoder.encode("aaaa1111!"))
                    .type(birthyear > 1984? "주니어":"시니어")
                    .build();

            LoginDto login = LoginDto.builder()
                    .userId(member.getUserId())
                    .loginId(loginId)
                    .name(name)
                    .password(passwordEncoder.encode("aaaa1111!"))
                    .type(member.getType())
                    .build();

            memberRepository.save(member);
            TokenInfo tokenInfo = tokenProvider.createToken(login);
            return tokenInfo;
        } else {
            Member member = findMember.get();

            LoginDto member1 = LoginDto.builder()
                    .userId(member.getUserId())
                    .loginId(member.getLoginId())
                    .password(member.getPassword())
                    .type(member.getType())
                    .name(member.getName())
                    .comment(member.getComment() == null? "" : member.getComment())
                    .score(member.getScore())
                    .build();

            TokenInfo tokenInfo = tokenProvider.createToken(member1);
            return tokenInfo;
        }
    }

    public GetNaverUserRes getUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        String userInfo = responseEntity.getBody();

        Gson gsonObj = new Gson();
        Map<?, ?> data = gsonObj.fromJson(userInfo, Map.class);
        String loginId = (String) ((Map<?, ?>) (data.get("response"))).get("email");
        String name = (String) ((Map<?, ?>) (data.get("response"))).get("nickname");
        String birthyear = (String) ((Map<?, ?>) (data.get("response"))).get("birthyear");
        return new GetNaverUserRes(loginId, name, birthyear);
    }
}
