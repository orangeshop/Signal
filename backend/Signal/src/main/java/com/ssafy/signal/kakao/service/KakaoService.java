package com.ssafy.signal.kakao.service;

import com.google.gson.Gson;
import com.ssafy.signal.kakao.dto.GetKakaoUserRes;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.LoginDto;
import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenInfo kakaoCallBack(String accessToken) throws Exception {
        GetKakaoUserRes getKakaoUserRes = getUserInfo(accessToken);
        String email = getKakaoUserRes.getEmail();
        String nickName = getKakaoUserRes.getNickName();
        Optional<Member> findUser = memberRepository.findByLoginId(email);
        if (!findUser.isPresent()) {
            String randomPassword = generateRandomPassword(8);

            Member member = Member.builder()
                    .loginId(email)
                    .name(nickName)
                    .password(passwordEncoder.encode(randomPassword))
                    .type("주니어")
                    .build();
            memberRepository.save(member);

            LoginDto login = LoginDto.builder()
                    .userId(member.getUserId())
                    .loginId(email)
                    .name(nickName)
                    .password(passwordEncoder.encode(randomPassword))
                    .type(member.getType())
                    .build();

            TokenInfo tokenInfo = tokenProvider.createToken(login);
            return tokenInfo;
        }
        else {
            Member member = findUser.get();
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


    public GetKakaoUserRes getUserInfo(String accessToken) throws Exception{
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        String userInfo = responseEntity.getBody();

        Gson gsonObj = new Gson();
        Map<?, ?> data = gsonObj.fromJson(userInfo, Map.class);

        boolean emailAgreement = (boolean) ((Map<?, ?>) (data.get("kakao_account"))).get("email_needs_agreement");
        String email;
        if (emailAgreement) {
            email = "";
        } else {
            email = (String) ((Map<?, ?>) (data.get("kakao_account"))).get("email");
        }

        boolean nickNameAgreement = (boolean) ((Map<?, ?>) (data.get("kakao_account"))).get("profile_nickname_needs_agreement");
        String nickName;
        if (nickNameAgreement) {
            nickName = "";
        } else {
            nickName = (String) ((Map<?, ?>) ((Map<?, ?>) data.get("properties"))).get("nickname");
        }
        return new GetKakaoUserRes(email, nickName);
    }

    public String generateRandomPassword(int length) {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String combinedChars = RandomStringUtils.random(length - 8, 48, 122, true, true);
        String totalChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(combinedChars);
        List<Character> pwdChars = totalChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
