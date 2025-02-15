package com.springboot.answer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.answer.entitiy.Answer;
import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerResponseDto {

    private Long answerId;

    private String content;

    public static AnswerResponseDto defaultMessage() {
        return new AnswerResponseDto(null, "답변까지 조금만 기다려주세요");
    }
}
