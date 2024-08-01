package com.ssafy.signal.member.service;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.domain.TokenBlacklist;
import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.member.repository.TokenBlacklistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;


    public Boolean chekcLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            log.info("이미 등록된 아이디 = {}", loginId);
            return false;
        } else {
            return true;
        }
    }

    public Member saveMember(Member member) {
        checkPasswordStrength(member.getPassword());

        if (memberRepository.existsByLoginId(member.getLoginId())) {
            log.info("이미 등록된 아이디 = {}", member.getLoginId());
            throw new IllegalStateException("이미 등록된 아이디입니다.");
        }
        Member member1 = Member.builder()
                .userId(member.getUserId())
                .loginId(member.getLoginId())
                .password(passwordEncoder.encode(member.getPassword()))
                .type(member.getType())
                .name(member.getName())
                .build();

        return memberRepository.save(member1);
    }


    public TokenInfo loginMember(String loginId, String password) {
        try {
            Member member = findMemberByLoginId(loginId);

            checkPassword(password, member);

            return tokenProvider.createToken(member);
        } catch (IllegalArgumentException | BadCredentialsException exception) {
//            throw new IllegalArgumentException("계정이 존재하지 않거나 비밀번호가 잘못되었습니다.");

            return TokenInfo.builder()
                    .status(false)
                    .member(null)
                    .tokenId(null)
                    .accessToken(null)
                    .accessTokenExpireTime(null)
                    .refreshToken(null)
                    .refreshTokenExpireTime(null)
                    .build();
        }
    }

    private void checkPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.info("일치하지 않는 비밀번호");
            throw new BadCredentialsException("기존 비밀번호 확인에 실패했습니다.");
        }
//        if (!password.equals(member.getPassword())) {
//            log.info("일치하지 않는 비밀번호");
//            throw new BadCredentialsException("기존 비밀번호 확인에 실패했습니다.");
//        }
    }

    private Member findMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() -> {
            log.info("계정이 존재하지 않음");
            return new IllegalArgumentException("계정이 존재하지 않습니다.");
        });
    }

    @Transactional
    public Member updateMember(Long userId, Member updatedMember) {
        Member existingMember = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
            existingMember.setPassword(passwordEncoder.encode(updatedMember.getPassword()));
        }
        if (updatedMember.getType() != null && !updatedMember.getType().isEmpty()) {
            existingMember.setType(updatedMember.getType());
        }
        if (updatedMember.getName() != null && !updatedMember.getName().isEmpty()) {
            existingMember.setName(updatedMember.getName());
        }
        if (updatedMember.getComment() != null && !updatedMember.getComment().isEmpty()) {
            existingMember.setComment(updatedMember.getComment());
        }

        return memberRepository.save(existingMember);
    }

    public Member getUserInfo(String loginId) {
        return findMemberByLoginId(loginId);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));
        return new org.springframework.security.core.userdetails.User(member.getLoginId(), member.getPassword(), new ArrayList<>());
    }

    private void checkPasswordStrength(String password) {
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            return;
        }

        log.info("비밀번호 정책 미달");
        throw new IllegalArgumentException("비밀번호는 최소 8자리에 영어, 숫자, 특수문자를 포함해야 합니다.");
    }

    public void deleteMemberByLoginId(String loginId) {
        memberRepository.deleteByLoginId(loginId);
    }

}