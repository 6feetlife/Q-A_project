package com.springboot.answer.dto;

import com.springboot.answer.entitiy.Answer;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerResponseDto {

    private Long answerId;

    private Question question;

    private String content;

    private Answer.AnswerStatus answerStatus;
}
