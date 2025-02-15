package com.springboot.member.mapper;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entitiy.Answer;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.answer.service.AnswerService;
import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.dto.MemberPostDto;
import com.springboot.member.dto.MemberResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.mapper.QuestionMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMapper {
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;

    public MemberMapper(AnswerService answerService, AnswerMapper answerMapper, QuestionMapper questionMapper) {
        this.answerService = answerService;
        this.answerMapper = answerMapper;
        this.questionMapper = questionMapper;
    }

    public Member memberPostDtoToMember(MemberPostDto memberPostDto){
        Member member = new Member();
        member.setName(memberPostDto.getName());
        member.setEmail(memberPostDto.getEmail());
        member.setPassword(memberPostDto.getPassword());
        member.setPhone(memberPostDto.getPhone());
        member.setNickname(memberPostDto.getNickname());

        return member;
    }
    public Member memberPatchDtoToMember(MemberPatchDto memberPatchDto) {
        Member member = new Member();

        member.setName(memberPatchDto.getName());
        member.setPassword(memberPatchDto.getPassword());
        member.setPhone(memberPatchDto.getPhone());
        member.setNickname(memberPatchDto.getNickname());

        return member;
    }

    public MemberResponseDto memberToMemberResponseDto(Member member) {
        MemberResponseDto memberResponseDto = new MemberResponseDto();

        memberResponseDto.setName(member.getName());
        memberResponseDto.setPhone(member.getPhone());
        memberResponseDto.setNickname(member.getNickname());
        memberResponseDto.setEmail(member.getEmail());

        List<QuestionResponseDto> questionResponseDtos = member.getQuestions().stream()
                            .map(question -> questionMapper.questionToQuestionResponseDto(question))
                            .collect(Collectors.toList());

        memberResponseDto.setQuestionResponseDtos(questionResponseDtos);

        return memberResponseDto;
    }

    public List<MemberResponseDto> pageToList (Page<Member> page) {

        List<MemberResponseDto> memberResponseDtos = page.stream()
                .map(member -> memberToMemberResponseDto(member))
                .collect(Collectors.toList());

        return memberResponseDtos;
    }

    public List<MemberResponseDto> membersToMemberResponseDtos(List<Member> members) {
        return members.stream().map(member -> memberToMemberResponseDto(member))
                    .collect(Collectors.toList());
    }

    // questionId 로 AnswerResponseDto 만들기
    public AnswerResponseDto findAnswerResponseDto(long questionId) {
        Answer answer = answerService.verifiedAnswerUseQuestionId(questionId);
        return answerMapper.AnswerToAnswerResponseDto(answer);
    }

}
