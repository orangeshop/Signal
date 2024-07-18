package com.ssafy.signal.member.controller;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.MemberLoginDto;
import com.ssafy.signal.member.jwt.JwtUtil;
import com.ssafy.signal.member.jwt.json.ApiResponseJson;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.principle.UserPrinciple;
import com.ssafy.signal.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponseJson createMember(@Valid @RequestBody Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        Member member1 = memberService.saveMember(member);
        log.info("Account successfully created: {}", member1);

        return new ApiResponseJson(HttpStatus.OK, Map.of(
                "loginId", member1.getLoginId(),
                "name", member1.getName()
        ));
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

    @DeleteMapping("/{id}")
    public String deleteMember(@PathVariable("id") Long id) {
        memberService.deleteMember(id);
        return "Delete successful";
    }

    @PostMapping("/login")
    public ApiResponseJson authenticateAccountAndIssueToken(@Valid @RequestBody MemberLoginDto memberLoginDto,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        TokenInfo tokenInfoDto = memberService.loginMember(memberLoginDto.getLoginId(), memberLoginDto.getPassword());
        log.info("Token issued for account: {}", tokenInfoDto.getTokenId());

        return new ApiResponseJson(HttpStatus.OK, tokenInfoDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            // JWT를 블랙리스트에 추가하는 등의 로그아웃 처리
        }
        return ResponseEntity.ok("Logout successful");
    }
}

