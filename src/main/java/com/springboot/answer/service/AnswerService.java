package com.springboot.answer.service;

import com.springboot.answer.entitiy.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.auth.utils.MemberDetails;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import com.springboot.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;
import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionService questionService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
    }


    public void createAnswer(Answer answer) {
        // answer 가 등록될 question 이 존재하는지 확인
        Question findQuestion = questionService.validateQuestionExistence(answer.getQuestion().getQuestionId());

        // question 상태가 QUESTION_REGISTERED 상태일때만 답변 등록 가능
        boolean value = Objects.equals(findQuestion.getQuestionStatus().name(), "QUESTION_REGISTERED");

        // answer 가 등록되면 question "답변 완료" 상태로 변경
        if(!value){
            throw new BusinessLogicException(ExceptionCode.ALREADY_ANSWER);
        } else {
            findQuestion.setQuestionStatus(Question.QuestionStatus.QUESTION_ANSWERED);
            answerRepository.save(answer);
        }
    }


    public void updateAnswer(Answer answer, long answerId) {

        // 존재하는 답변인지 검증
        Answer findAnswer = verifiedAnswer(answerId);

        findAnswer.setContent(
                Optional.ofNullable(answer.getContent())
                        .orElse(findAnswer.getContent())
        );

        answerRepository.save(findAnswer);
    }

    @Transactional
    public void deleteAnswer(long answerId) {
        Answer findAnswer = verifiedAnswer(answerId);
        answerRepository.delete(findAnswer);
        findAnswer.getQuestion().setQuestionStatus(Question.QuestionStatus.QUESTION_REGISTERED);
    }

    // answer 존재하는지 검증 메서드
    public Answer verifiedAnswer(long answerId) {
        Optional<Answer> findAnswer = answerRepository.findByAnswerId(answerId);
        return findAnswer.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
    }

    // questionId 로 Answer 찾기
    public Answer verifiedAnswerUseQuestionId(long questionId) {
        Optional<Answer> findAnswer = answerRepository.findByQuestion_QuestionId(questionId);
        return findAnswer.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
    }

}
