package com.springboot.answer.dto;

import com.springboot.answer.entitiy.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
public class AnswerPostDto {

    @Positive
    private Long answerId;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z가-힣!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?`~ ]{1,800}$")
    private String content;

    private Answer.AnswerStatus answerStatus = Answer.AnswerStatus.DONE_ANSWER;
}
