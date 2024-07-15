package com.ssafy.signal.member.controller;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.MemberRequestDto;
import com.ssafy.signal.member.service.MemberService;

import lombok.RequiredArgsConstructor;

import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// https://sjh9708.tistory.com/78
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final Member member;

    private final MemberService memberService;

    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public ResponseEntity<Long> addMember(@Valid @RequestBody MemberRequestDto member){
        Member entity = modelMapper.map(member, Member.class);
        Long id = memberService.join(entity);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }
}
