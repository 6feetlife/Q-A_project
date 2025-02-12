package com.springboot.question.service;

import com.springboot.question.entity.Question;
import com.springboot.question.questionRepository.QuestionRepository;
import com.springboot.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;

    public QuestionService(QuestionRepository questionRepository, UserService userService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
    }

    @PostMapping
    public Question createQuestion (Question question) {

    }



    @PatchMapping



    @GetMapping




    @GetMapping




    @DeleteMapping
}
