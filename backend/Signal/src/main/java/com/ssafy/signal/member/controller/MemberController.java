package com.ssafy.signal.member.controller;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.MemberLoginDto;
import com.ssafy.signal.member.dto.MyProfileDto;
import com.ssafy.signal.member.dto.MypageUpdateDto;
import com.ssafy.signal.member.dto.findMemberDto;
import com.ssafy.signal.member.json.duplicateJson;
import com.ssafy.signal.member.jwt.JwtUtil;
import com.ssafy.signal.member.jwt.json.ApiResponseJson;
import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.jwt.token.dto.TokenRequest;
import com.ssafy.signal.member.principle.UserPrinciple;
import com.ssafy.signal.member.service.MemberService;

import com.ssafy.signal.member.service.TokenBlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.checkerframework.checker.units.qual.A;
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
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<findMemberDto> getMemberById(@PathVariable("id") Long id) {
        try {
            findMemberDto member = memberService.findMemberById(id);
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
//            log.info("Token issued for account: {}", tokenInfoDto.getTokenId());

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

    @PostMapping("/duplicate/{loginId}")
    public duplicateJson duplicateId(@PathVariable("loginId") String loginId) {
        if (memberService.chekcLoginId(loginId)) {
            return new duplicateJson(false);
        } else {
            return new duplicateJson(true);
        }
    }


    @GetMapping("/mypage")
    public ApiResponseJson getUserInfo(@AuthenticationPrincipal UserPrinciple userPrinciple) {

        if (userPrinciple != null) {
            log.info("요청 아이디 : {}", userPrinciple.getLoginId());

            MyProfileDto foundMember = memberService.getUserInfo(userPrinciple.getLoginId());

            return new ApiResponseJson(HttpStatus.OK, foundMember);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }



    @PutMapping("/{id}")
    public MypageUpdateDto updateMember(@RequestBody Member member, @PathVariable("id") Long id) {

        return memberService.updateMember(id, member);
    }


    @DeleteMapping("/drop")
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal UserPrinciple userPrinciple,@RequestHeader("RefreshToken") String bearerToken) {
        String token = tokenProvider.resolveToken(bearerToken);
        if (token != null) {
            // 현재 로그인한 사용자를 확인하고 회원 탈퇴 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info(String.valueOf(authentication.isAuthenticated()));
            if (authentication != null && authentication.isAuthenticated()) {
                String loginId = authentication.getName();

                // 회원 탈퇴 처리
                memberService.deleteMemberByLoginId(loginId);

                // 토큰 블랙리스트에 추가하여 로그아웃 처리
//                Instant expirationInstant = tokenProvider.getExpiration(token).toInstant();
//                LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationInstant, ZoneId.systemDefault());
//                tokenBlacklistService.blacklistToken(token, expirationTime);
                memberService.logout(token,userPrinciple.getLoginId());

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
//        log.info("Token issued for account: {}", tokenInfoDto.getAccessToken());

//        return new ApiResponseJson(HttpStatus.OK, tokenInfoDto);
        return tokenInfoDto;
    }

    @PostMapping("/autologin")
    public TokenInfo autoauthenticateAccountAndIssueToken(@Valid @RequestBody MemberLoginDto memberLoginDto,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        TokenInfo tokenInfoDto = memberService.autologinMember(memberLoginDto.getLoginId(), memberLoginDto.getPassword());
        log.info("AutoToken issued for account: {}", tokenInfoDto.getAccessToken());

        return tokenInfoDto;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrinciple userPrinciple,
            @RequestHeader("RefreshToken") String bearerToken) {
        String token = tokenProvider.resolveToken(bearerToken);
        log.debug("Extracted token for logout: {}", token);
        if (token != null) {
            memberService.logout(token,userPrinciple.getLoginId());
//            Instant expirationInstant = tokenProvider.getExpiration(token).toInstant();
//            LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationInstant, ZoneId.systemDefault());
//            tokenBlacklistService.blacklistToken(token, expirationTime);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).body(null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refreshToken(@RequestHeader("RefreshToken") String tokenRequest) {
        try {
//            String token = tokenProvider.resolveToken(tokenRequest);
            TokenInfo tokenInfo = tokenProvider.refreshToken(tokenRequest);
            log.info("refreshToken 재발급 되었습니다.");
            return ResponseEntity.ok(tokenInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 자기가 쓴 글 확인하기
    @GetMapping("/board/{userId}")
    public List<BoardDto> getMemberWithPosts(@PathVariable("userId") Long userId) throws Exception{
        return memberService.getMemberWithPosts(userId);
    }

    // 자기가 쓴 댓글의 글 확인하기
    @GetMapping("/comment/{userId}")
    public List<BoardDto> getMemberCommentedPosts(@PathVariable("userId") Long userId) throws Exception {
        return memberService.getMemberCommentedPosts(userId);
    }
}

