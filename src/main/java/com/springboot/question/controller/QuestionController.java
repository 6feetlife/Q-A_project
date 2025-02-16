package com.springboot.question.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.List;
import java.util.Set;

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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity postQuestion(@RequestPart("data") String data,
                                       @RequestPart(value = "image", required = false) MultipartFile image,
                                       @AuthenticationPrincipal MemberDetails memberDetails) throws IOException {
        // objectMapper 객체 생성 (JSON 문자열을 DTO 객체로 변환하는데 사용)
        ObjectMapper objectMapper = new ObjectMapper();
        // JSON 문자열(data)을 QuestionPostDto 객체로 변환
        QuestionPostDto requestBody = objectMapper.readValue(data, QuestionPostDto.class);

        // Spring Validator 를 사용하여 유효성 검사 실행
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        // QuestionPostDto 에 설정해둔 유효성 검사 설정을 가져와서 실행
        Set<ConstraintViolation<QuestionPostDto>> violations = validator.validate(requestBody);

        // 만약 검증에서 걸려서 통과되지 못한다면 에러코드 반환
        if(!violations.isEmpty()) {
            return new ResponseEntity(
                    new SingleResponseDto<>(violations), HttpStatus.BAD_REQUEST
            );
        }

        questionService.createQuestion(questionMapper.questionPostDtoToQuestion(requestBody),memberDetails, image);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{questionId}", consumes = "multipart/form-data")
    public ResponseEntity patchQuestion(@RequestPart("data") String data,
                                        @RequestPart(value = "image", required = false) MultipartFile image,
                                        @PathVariable("questionId") long questionId,
                                        @AuthenticationPrincipal MemberDetails memberDetails) throws JsonProcessingException {
        // objectMapper 객체 생성 (JSON 문자열을 DTO 객체로 변환하는데 사용)
        ObjectMapper objectMapper = new ObjectMapper();
        // JSON 문자열(data)을 QuestionPostDto 객체로 변환
        QuestionPatchDto requestBody = objectMapper.readValue(data, QuestionPatchDto.class);

        // Spring Validator 를 사용하여 유효성 검사 실행
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        // QuestionPatchDto 에 설정해둔 유효성 검사 설정을 가져와서 실행
        Set<ConstraintViolation<QuestionPatchDto>> violations = validator.validate(requestBody);

        Question question = questionMapper.questionPatchDtoToQuestion(requestBody);
        questionService.updateQuestion(question, memberDetails, image, questionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity getQuestion(@Valid @PathVariable("questionId") int questionId,
                                      @AuthenticationPrincipal MemberDetails memberDetails) {

        Question question = questionService.findQuestion(questionId, memberDetails);
        // 최신글은 title 앞에 *NEW* 붙여서 출력 (최신글 기준 = 2일)
        questionService.isNewPost(question);
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
        questionService.isNewPostList(questions);

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
