package com.springboot.member.service;

import com.springboot.auth.service.TokenService;
import com.springboot.auth.utils.CustomAuthorityUtils;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.question.entity.Question;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service

public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;
    private final TokenService tokenService;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
                         CustomAuthorityUtils authorityUtils, TokenService tokenService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
        this.tokenService = tokenService;
    }


    public Member createMember(Member member) {
        // 중복된 회원인지 이메일로 검증
        isMemberAlreadyRegistered(member);
        // 중복된 닉네임인지 검증
        isNicknameAlreadyUsed(member);

        List<String> roles = authorityUtils.createRoles(member.getEmail());
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        member.setRoles(roles);

        return memberRepository.save(member);
    }


    public Member updateMember(Member member, int memberId, String authorization) {
        // MemberId 로 존재하는 회원인지 검증
        Member findMember = validateExistingMember(memberId);
        validateMemberStatus(findMember);

        // 현재 요청한 사용자의 토큰에서 email 추출
        String currentUserEmail = tokenService.getUserIdFromToken(authorization);
        // uri 에 있는 memberId 의 owner email 추출
        String isOwnerEmail = findMember.getEmail();

        // 만약 요청한 사용자의 이메일과 변경하고자하는 유저정보의 owner 의 이메일이 같다면 변경 실행
        if(Objects.equals(currentUserEmail, isOwnerEmail)){
            findMember.setNickname(
                    Optional.ofNullable(member.getNickname())
                            .orElse(findMember.getName()));
            findMember.setName(
                    Optional.ofNullable(member.getName())
                            .orElse(findMember.getName()));
            findMember.setMemberStatus(
                    Optional.ofNullable(member.getMemberStatus())
                            .orElse(findMember.getMemberStatus()));
            findMember.setPassword(
                    Optional.ofNullable(passwordEncoder.encode(member.getPassword()))
                            .orElse(findMember.getPassword()));
            findMember.setPhone(
                    Optional.ofNullable(member.getPhone())
                            .orElse(findMember.getPhone()));
        }
        return memberRepository.save(findMember);
    }


    public Member findMember(int memberId) {
        // 유저 존재 확인
        Member findMember = validateExistingMember(memberId);

        return findMember;
    }


    public Page<Member> findMembers(int page, int size) {
        Page<Member> members = memberRepository.findAll(PageRequest.of(page, size, Sort.by("MemberId").ascending()));

        return members;
    }


    public void deleteMember(int memberId) {
        // 존재하는 회원인지 검증
        Member member = validateExistingMember(memberId);

        // 회원 상태가 활동중인지 검증
        // 활동중인 경우에만 삭제 가능 ( 보통 휴면계정 또한 휴면을 풀어야 삭제든 뭐든 가능)
        if(member.getMemberStatus() != Member.MemberStatus.ACTIVE_MEMBER){
            throw new BusinessLogicException(ExceptionCode.MEMBER_DEACTIVATED);
        }

        member.setMemberStatus(Member.MemberStatus.DEACTIVATED_MEMBER);
        member.getQuestions().stream()
                .forEach(question -> question.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED));

        memberRepository.save(member);
    }

    // 이미 가입된 회원인지 중복 가입 예외처리
    public void isMemberAlreadyRegistered(Member member) {
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EXISTING_MEMBER);
        }
    }

    // 이미 사용중인 닉네임인지 닉네임 예외처리
    public void isNicknameAlreadyUsed(Member member) {
        Optional<Member> findMember = memberRepository.findByNickname(member.getNickname());
        if(findMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_ALREADY_USED);
        }
    }

    // 가입된 회원인지 검증
    public Member validateExistingMember(int memberId) {
        Optional<Member> findMember = memberRepository.findByMemberId(memberId);
        return findMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    // 회원 상태 검증
    public void validateMemberStatus(Member member) {
        if (member.getMemberStatus() == Member.MemberStatus.DORMANT_MEMBER) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_DORMANT);
        } else if(member.getMemberStatus() == Member.MemberStatus.DEACTIVATED_MEMBER) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_DEACTIVATED);
        }
    }

}
