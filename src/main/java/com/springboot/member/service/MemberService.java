package com.springboot.member.service;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Member createMember(Member member) {
        // 중복된 회원인지 이메일로 검증
        isMemberAlreadyRegistered(member);
        // 중복된 닉네임인지 검증
        isNicknameAlreadyUsed(member);

        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);

        return memberRepository.save(member);
    }


    public Member updateMember(Member member) {
        // MemberId 로 존재하는 회원인지 검증
        Member findMember = validateExistingMember(member.getMemberId().intValue());
        validateMemberStatus(findMember);

        findMember.setNickname(
                Optional.ofNullable(member.getNickname())
                        .orElse(findMember.getName()));

        findMember.setMemberStatus(
                Optional.ofNullable(member.getMemberStatus())
                        .orElse(findMember.getMemberStatus()));

        findMember.setPassword(
                Optional.ofNullable(member.getPassword())
                        .orElse(findMember.getPassword()));

        findMember.setPhone(
                Optional.ofNullable(member.getPhone())
                        .orElse(findMember.getPhone()));

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
        Member member = validateExistingMember(memberId);
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
