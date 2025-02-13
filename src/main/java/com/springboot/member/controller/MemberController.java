package com.springboot.member.controller;

import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.dto.MemberPostDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v11/members")
@Slf4j
@Validated
public class MemberController {
    private final MemberMapper memberMapper;
    private final MemberService memberService;

    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberPostDto requestBody) {
        Member member = memberMapper.memberPostDtoToMember(requestBody);
        memberService.createMember(member);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity patchMember(@Valid @RequestBody MemberPatchDto requestBody,
                                      @PathVariable("memberId") int memberId,
                                      @RequestHeader("Authorization") String authorizationHeader) {
        Member member = memberMapper.memberPatchDtoToMember(requestBody);
        memberService.updateMember(member, memberId, authorizationHeader);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
