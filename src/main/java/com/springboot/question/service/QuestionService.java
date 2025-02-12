package com.springboot.question.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.like.repository.LikeRepository;
import com.springboot.question.entity.Question;
import com.springboot.question.questionRepository.QuestionRepository;
import com.springboot.response.PageInfo;
import com.springboot.user.entity.User;
import com.springboot.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;


    public QuestionService(QuestionRepository questionRepository, UserService userService,
                           LikeRepository likeRepository) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.likeRepository = likeRepository;

    }

    @PostMapping
    public Question createQuestion (Question question) {
        userService.validateExistingUser(question.getUser().getUserId().intValue());
        userService.validateUserStatus(question.getUser());

        return questionRepository.save(question);
    }

    @PatchMapping
    public void updateQuestion (Question question, User user) {
        // 존재하는 질문인지 검증
        Question findQuestion = validateQuestionExistence(question.getQuestionId().intValue());

        // 업데이트할 question 을 요청한 user 가 작성한게 맞는지 검증
        Question allowdQuestion = questionsByPermission(findQuestion, user);

        // 질문의 상태가 비활성화 및 삭제 상태라면 변경 불가
        if(question.getQuestionStatus() != Question.QuestionStatus.QUESTION_REGISTERED) {
            throw new BusinessLogicException(ExceptionCode.UNCHANGABLE_STATE);
        } else {
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
        }

        questionRepository.save(findQuestion);
    }

    @GetMapping
    public Question findQuestion(int questionId, User user) {
        // question 검증
        Question findQuestion = validateQuestionExistence(questionId);
        // 삭제 상태라면 예외 처리
        isVisible(findQuestion);

        // like 갯수 셋팅
        int likeCount = likeRepository.findByLikeId((long)questionId).size();
        findQuestion.setLikeCount(likeCount);

        // 권한에 따른 질문 조회 제한
        return questionsByPermission(findQuestion, user);
    }

    @GetMapping
    public Page<Question> questions(int page, int size) {
        Page<Question> questions = questionRepository.findAll(PageRequest.of(page, size, Sort.by("likeCount").descending()));
        return questions;
    }



    @DeleteMapping

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
    public Question questionsByPermission(Question findQuestion, User user) {
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
