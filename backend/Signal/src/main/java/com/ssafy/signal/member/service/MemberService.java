package com.ssafy.signal.member.service;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(Long userId, Member updatedMember) {
        Member existingMember = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

//        if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
//            existingMember.setPassword(passwordEncoder.encode(updatedMember.getPassword()));
//        }
        if (updatedMember.getType() != null && !updatedMember.getType().isEmpty()) {
            existingMember.setType(updatedMember.getType());
        }
        if (updatedMember.getName() != null && !updatedMember.getName().isEmpty()) {
            existingMember.setName(updatedMember.getName());
        }

        return memberRepository.save(existingMember);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
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

}