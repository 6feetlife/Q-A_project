package com.springboot.answer.dto;

import com.springboot.answer.entitiy.Answer;
import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResponseDto {

    private Long answerId;

    private String content;
}
