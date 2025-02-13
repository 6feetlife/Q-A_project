package com.springboot.question.dto;

import com.springboot.answer.entitiy.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class QuestionResponseDto {

    private Long memberId;

    private String userNickname;

    private Answer answer;

    private String title;

    private String contents;

    private int likeCount;
}
