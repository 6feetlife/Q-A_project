package com.springboot.likes.service;

import com.springboot.auth.utils.MemberDetails;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.likes.entity.Likes;
import com.springboot.likes.repository.LikesRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final MemberService memberService;
    private final QuestionService questionService;


    public LikesService(LikesRepository likesRepository, MemberService memberService, QuestionService questionService) {
        this.likesRepository = likesRepository;
        this.memberService = memberService;
        this.questionService = questionService;
    }

//    public Likes createLikes (long questionId, MemberDetails memberDetails) {
//        // 존재하는 질문인지 검증
//        Question findQuestion = questionService.validateQuestionExistence(questionId);
//        // 요청한 회원 찾기
//        Member findMember = memberService.validateExistingMemberUsedEmail(memberDetails.getEmail());
//        // 활동중인 회원인지 검증
//        memberService.validateMemberStatus(findMember);
//
//        // 이미 좋아요를 눌렀던 회원이라면 예외처리
//        Optional<Likes> findLikes = likesRepository.findByMember_MemberId(findMember.getMemberId());
//
//        // 값이 있다면 예외처리
//        if(findLikes.isPresent()) {
//            new BusinessLogicException(ExceptionCode.LIKE_ALREADY_GIVEN);
//        }
//
//        // 새로운 like 생성
//        Likes likes = new Likes();
//        likes.setQuestion(findQuestion);
//        likes.setMember(findMember);
//
//        findQuestion.setLikeCount(findQuestion.getLikeCount() + 1);
//
//        return likesRepository.save(likes);
//    }

    public String createLikes (long questionId, MemberDetails memberDetails) {
        // 존재하는 질문인지 검증
        Question findQuestion = questionService.validateQuestionExistence(questionId);
        // 요청한 회원 찾기
        Member findMember = memberService.validateExistingMemberUsedEmail(memberDetails.getEmail());
        // 활동중인 회원인지 검증
        memberService.validateMemberStatus(findMember);

        // 해당 멤버가 누른 좋아요 목록 조회
        List<Likes> likesList = likesRepository.findByMember_MemberId(findMember.getMemberId());

        // questionId 와 memberId 모두 만족하는 likes 객체 찾기
        Optional<Likes> matchedLikes= likesList.stream()
                .filter(likes ->
                        Objects.equals(likes.getQuestion().getQuestionId(), findQuestion.getQuestionId()) &&
                        Objects.equals(likes.getMember().getMemberId(), findMember.getMemberId())).findFirst();

        // questionId 와 memberId 의 두 조건을 모두 만족하는 likes 가 있다면 likes 삭제 없다면 생성
        if(matchedLikes.isPresent()) {
            // 질문글에 likeCount -1 감소
            findQuestion.setLikeCount(findQuestion.getLikeCount() - 1);
            likesRepository.delete(matchedLikes.get());
            return "좋아요가 취소되었습니다";
        } else {
            // 새로운 like 생성
            Likes likes = new Likes();
            likes.setQuestion(findQuestion);
            likes.setMember(findMember);
            // 질문글에 likeCount +1 증가
            findQuestion.setLikeCount(findQuestion.getLikeCount() + 1);
            likesRepository.save(likes);

            return "좋아요가 추가되었습니다";
        }

    }
}
