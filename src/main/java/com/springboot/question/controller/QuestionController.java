package com.springboot.question.controller;

import com.springboot.auth.utils.MemberDetails;
import com.springboot.question.dto.QuestionPatchDto;
import com.springboot.question.dto.QuestionPostDto;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.entity.Question;
import com.springboot.question.mapper.QuestionMapper;
import com.springboot.question.questionRepository.QuestionRepository;
import com.springboot.question.service.QuestionService;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/v11/questions")
@Slf4j
@Validated
public class QuestionController {
    private final QuestionMapper questionMapper;
    private final QuestionService questionService;

    public QuestionController(QuestionMapper questionMapper, QuestionService questionService) {
        this.questionMapper = questionMapper;
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity postQuestion(@Valid @RequestBody QuestionPostDto requestBody,
                                       @AuthenticationPrincipal MemberDetails memberDetails) {
        requestBody.setMemberId(memberDetails.getMemberId());
        questionService.createQuestion(questionMapper.questionPostDtoToQuestion(requestBody), memberDetails);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{questionId}")
    public ResponseEntity patchQuestion(@Valid @RequestBody QuestionPatchDto requestBody,
                                      @PathVariable("questionId") long questionId,
                                        @AuthenticationPrincipal MemberDetails memberDetails) {
        requestBody.setQuestionId(questionId);
        Question question = questionMapper.questionPatchDtoToQuestion(requestBody);
        questionService.updateQuestion(question, memberDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity getQuestion(@Valid @PathVariable("questionId") int questionId,
                                      @AuthenticationPrincipal MemberDetails memberDetails) {

        Question question = questionService.findQuestion(questionId, memberDetails);
        QuestionResponseDto response = questionMapper.questionToQuestionResponseDto(question);
        return new ResponseEntity<>(
                new SingleResponseDto<>(response), HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity getQuestions(@Positive @RequestParam("page") int page,
                                     @Positive @RequestParam("size") int size) {

        Page<Question> pageQuestion = questionService.findQuestions(page - 1, size);

        List<Question> questions = pageQuestion.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        questionMapper.questionsToQuestionResponseDto(questions), pageQuestion), HttpStatus.OK
        );
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity deleteMember(@Valid @PathVariable("questionId") int questionId,
                                       @AuthenticationPrincipal MemberDetails memberDetails) {
        questionService.deleteQuestion(questionId, memberDetails);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
