package com.springboot.answer.controller;

import com.springboot.answer.dto.AnswerPatchDto;
import com.springboot.answer.dto.AnswerPostDto;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.answer.service.AnswerService;
import com.springboot.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v11/answers")
@Slf4j
@Validated
public class AnswerController {
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;


    public AnswerController(AnswerService answerService, AnswerMapper answerMapper) {
        this.answerService = answerService;
        this.answerMapper = answerMapper;
    }

    @PostMapping
    public ResponseEntity postAnswer(@Valid @RequestBody AnswerPostDto answerPostDto) {

        answerService.createAnswer(answerMapper.answerPostDtoToAnswer(answerPostDto));

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{answerId}")
    public ResponseEntity patchAnswer(@Valid @RequestBody AnswerPatchDto answerPatchDto,
                                      @Positive @PathVariable("answerId") long answerId) {

        answerService.updateAnswer(answerMapper.answerPatchDtoToAnswer(answerPatchDto), answerId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity deleteAnswer(@Positive @PathVariable("answerId") long answerId) {
        answerService.deleteAnswer(answerId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
