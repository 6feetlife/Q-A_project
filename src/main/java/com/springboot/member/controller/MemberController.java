package com.springboot.member.controller;

import com.springboot.member.dto.MemberPostDto;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v11/members")
@Slf4j
@Validated
public class MemberController {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public MemberController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberPostDto requestBody) {


    }
}
