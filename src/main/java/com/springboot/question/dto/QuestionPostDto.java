package com.springboot.question.dto;

import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@Setter
public class QuestionPostDto {


    private long memberId;

    @NotBlank
    @Size(max = 500, message = "내용은 500자 이하로 입력해야 합니다.")
    private String content;

    @NotBlank
    @Size(max = 100, message = "제목은 100자 이하로 입력해야 합니다.")
    private String title;


    private Question.QuestionVisibilityScope questionVisibilityScope = Question.QuestionVisibilityScope.PUBLIC_QUESTION;
}
