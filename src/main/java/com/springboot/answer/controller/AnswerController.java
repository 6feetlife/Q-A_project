package com.springboot.answer.controller;

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

        answerService.createAnswer(answerMapper.AnswerToAnswerPostDto(answerPostDto));

        return new ResponseEntity(HttpStatus.OK);
    }

}
