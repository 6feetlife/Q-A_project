package com.springboot.question.service;


import com.springboot.auth.utils.MemberDetails;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.likes.entity.Likes;
import com.springboot.likes.repository.LikesRepository;
import com.springboot.question.entity.Question;
import com.springboot.question.questionRepository.QuestionRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberService memberService;
    private final LikesRepository likesRepository;


    @Value("${mail.address.admin}")
    private String adminEmail;

    public QuestionService(QuestionRepository questionRepository, MemberService memberService,
                           LikesRepository likesRepository) {
        this.questionRepository = questionRepository;
        this.memberService = memberService;
        this.likesRepository = likesRepository;
    }

    public Question createQuestion (Question question, MemberDetails memberDetails) {

        // question 에 담겨온 memberId 로 member 존재하는지 확인
        Member findMember = memberService.validateExistingMember(question.getMember().getMemberId().intValue());

        // uri 에 있는 memberId 의 owner email 추출
        String isOwnerEmail = findMember.getEmail();

        if(Objects.equals(memberDetails.getEmail(), isOwnerEmail)) {
            // 질문등록이 가능한 상태인지 검증
            memberService.validateMemberStatus(question.getMember());
        }

        question.setMember(findMember);
        Question savedQuestion = questionRepository.save(question);

        Likes likes = new Likes();
        likes.setMember(findMember);
        likes.setQuestion(question);
        likesRepository.save(likes);

        return savedQuestion;
    }


    public void updateQuestion (Question question, MemberDetails memberDetails) {
        // 존재하는 질문인지 검증
        Question findQuestion = validateQuestionExistence(question.getQuestionId().intValue());

        // 수정할 질문글을 작성한 member email 추출
        String ownerEmail = findQuestion.getMember().getEmail();

        // 질문의 상태가 비활성화 및 삭제 상태라면 변경 불가
        if(question.getQuestionStatus() != Question.QuestionStatus.QUESTION_REGISTERED) {
            throw new BusinessLogicException(ExceptionCode.UNCHANGABLE_STATE);
        }
        if (Objects.equals(ownerEmail, memberDetails.getEmail())){
            findQuestion.setContent(
                    Optional.ofNullable(question.getContent())
                            .orElse(findQuestion.getContent())
            );

            findQuestion.setTitle(
                    Optional.ofNullable(question.getTitle())
                            .orElse(findQuestion.getTitle())
            );
            findQuestion.setQuestionVisibilityScope(
                    Optional.ofNullable(question.getQuestionVisibilityScope())
                            .orElse(findQuestion.getQuestionVisibilityScope())
            );
        } else {
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
        }

        questionRepository.save(findQuestion);
    }


    public Question findQuestion(int questionId, MemberDetails memberDetails) {
        // question 존재하는지 검증
        Question findQuestion = validateQuestionExistence(questionId);
        // 삭제 상태라면 예외 처리
        isVisible(findQuestion);

        // like 갯수 셋팅
        int likeCount = likesRepository.findByQuestion_QuestionId((long)questionId).size();
        findQuestion.setLikeCount(likeCount);

        // viewCount 증가
        findQuestion.setViewCount(findQuestion.getViewCount() + 1);

        // 질문글에 등록된 member email 추출
        String ownerEmail = findQuestion.getMember().getEmail();

        // 비밀글인경우
        if(findQuestion.getQuestionVisibilityScope() == Question.QuestionVisibilityScope.PRIVATE_QUESTION){
            // 유저 정보 owner 의 email 과 관리자 email 을 담은 리스트
            List<String> authentication = List.of(ownerEmail, adminEmail);

            // 요청한 유저의 이메일과 비교하여 리스트에 동일한 이메일이 있는지 true / false
            boolean valuer = authentication.stream()
                    .anyMatch(email -> Objects.equals(email, memberDetails.getEmail()));

            // 요청한 유저가 조회하고자 하는 유저 정보의 owner 와 동일 인물인지 또는 관리자인지 권한에 따른 접근 제한
            if(!valuer) {
                throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
            } else {

                return findQuestion;
            }
        } else {
            return findQuestion;
        }

    }

    public Page<Question> findQuestions(int page, int size) {
        Page<Question> questions = questionRepository.findAll(PageRequest.of(page, size, Sort.by("likeCount").descending()));
        return questions;

    }

    public void deleteQuestion(int questionId, MemberDetails memberDetails) {
        // question 존재하는지 검증
        Question findQuestion = validateQuestionExistence(questionId);
        // 삭제 상태라면 예외 처리
        isVisible(findQuestion);

        // 질문글에 등록된 member email 추출
        String ownerEmail = findQuestion.getMember().getEmail();

        // 요청한 유저의 이메일과 비교하여 리스트에 동일한 이메일이 있는지 true / false
        boolean valuer = Objects.equals(ownerEmail,memberDetails.getEmail());

        // 요청한 유저가 조회하고자 하는 유저 정보의 owner 와 동일 인물인지 또는 관리자인지 권한에 따른 접근 제한
        if(!valuer) {
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
        } else {
            findQuestion.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED);
            questionRepository.save(findQuestion);
        }
    }



    // question 존재 확인
    public Question validateQuestionExistence(int questionId) {
        Optional<Question> findQuestion = questionRepository.findByQuestionId(questionId);

        return findQuestion.orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }

    // question 삭제 상태 확인
    public void isVisible(Question question) {
        if(question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND);
        }
    }

    // 권한에 따른 질문 조회 제한
    public Question questionsByPermission(Question findQuestion, Member user) {
        // user 가 가지고있는 question 리스트에 해당 questionId 가 포함되어있는지 true or false
        boolean value = user.getQuestions().stream()
                .anyMatch(question -> Objects
                        .equals(question.getQuestionId(), findQuestion.getQuestionId()));

        // question 상태가 공개 상태라면 question 반환
        if (findQuestion.getQuestionVisibilityScope() == Question.QuestionVisibilityScope.PUBLIC_QUESTION) {
            return findQuestion;
            // 만약 아니라면 question 유저 id 와 해당 유저가 조회 대상인 question 을 등록한게 맞는지 검증
        } else if (value) {
            return findQuestion;
        } else {
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
        }
    }
}
