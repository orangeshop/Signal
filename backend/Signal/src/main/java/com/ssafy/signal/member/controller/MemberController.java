package com.ssafy.signal.member.controller;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.MemberLoginDto;
import com.ssafy.signal.member.jwt.JwtUtil;
import com.ssafy.signal.member.jwt.json.ApiResponseJson;
import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.principle.UserPrinciple;
import com.ssafy.signal.member.service.MemberService;

import com.ssafy.signal.member.service.TokenBlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Endpoint accessible");
    }

    @GetMapping("/all")
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable("id") Long id) {
        try {
            Member member = memberService.getMemberById(id);
            return ResponseEntity.ok(member);
        } catch (NoSuchElementException e) {
            // 예외가 발생한 경우 적절한 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // 그 외의 예외에 대해 500 에러를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/create")
    public TokenInfo createMember(@Valid @RequestBody Member member, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("잘못된 요청입니다.");
            }

            Member member1 = memberService.saveMember(member);
            log.info("Account successfully created: {}", member1);

            TokenInfo tokenInfoDto = memberService.loginMember(member.getLoginId(), member.getPassword());
            log.info("Token issued for account: {}", tokenInfoDto.getTokenId());

            return tokenInfoDto;

//            return new ApiResponseJson(HttpStatus.OK, Map.of(
//                    "loginId", member1.getLoginId(),
//                    "name", member1.getName()
//            )
//            )
        } catch (IllegalArgumentException | BadCredentialsException exception) {
            TokenInfo tokenInfoDto2 = memberService.loginMember(null, null);
            return tokenInfoDto2;
//            return new ApiResponseJson(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PostMapping("/duplicate")
    public ResponseEntity<String> duplicateId(@RequestParam String loginId) {
        if (memberService.chekcLoginId(loginId)) {
            return ResponseEntity.ok("사용가능한 아이디입니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복되는 아이디가 있습니다.");
        }
    }


    @GetMapping("/userinfo")
    public ApiResponseJson getUserInfo(@AuthenticationPrincipal UserPrinciple userPrinciple) {

        log.info("요청 아이디 : {}", userPrinciple.getLoginId());

        Member foundMember = memberService.getUserInfo(userPrinciple.getLoginId());

        return new ApiResponseJson(HttpStatus.OK, foundMember);
    }

    @PutMapping("/{id}")
    public Member updateMember(@RequestBody Member member, @PathVariable("id") Long id) {
        return memberService.updateMember(id, member);
    }


    @DeleteMapping("/drop")
    public ResponseEntity<String> deleteMember(@RequestHeader("Authorization") String bearerToken) {
        String token = tokenProvider.resolveToken(bearerToken);
        if (token != null) {
            // 현재 로그인한 사용자를 확인하고 회원 탈퇴 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String loginId = authentication.getName();

                // 회원 탈퇴 처리
                memberService.deleteMemberByLoginId(loginId);

                // 토큰 블랙리스트에 추가하여 로그아웃 처리
                Instant expirationInstant = tokenProvider.getExpiration(token).toInstant();
                LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationInstant, ZoneId.systemDefault());
                tokenBlacklistService.blacklistToken(token, expirationTime);

                return ResponseEntity.ok("Delete successful");
            } else {
                return ResponseEntity.status(403).body("Unauthorized access");
            }
        }
        return ResponseEntity.status(400).body("Invalid token");
    }

    @PostMapping("/login")
    public TokenInfo authenticateAccountAndIssueToken(@Valid @RequestBody MemberLoginDto memberLoginDto,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        TokenInfo tokenInfoDto = memberService.loginMember(memberLoginDto.getLoginId(), memberLoginDto.getPassword());
        log.info("Token issued for account: {}", tokenInfoDto.getTokenId());

//        return new ApiResponseJson(HttpStatus.OK, tokenInfoDto);
        return tokenInfoDto;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        String token = tokenProvider.resolveToken(bearerToken);
        log.debug("Extracted token for logout: {}", token);
        if (token != null) {
            Instant expirationInstant = tokenProvider.getExpiration(token).toInstant();
            LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationInstant, ZoneId.systemDefault());
            tokenBlacklistService.blacklistToken(token, expirationTime);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).body(null);
    }
}

