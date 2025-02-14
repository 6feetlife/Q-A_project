package com.springboot.question.dto;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entitiy.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionResponseDto {

    private Long questionId;

    private Long memberId;

    private String userNickname;

    private AnswerResponseDto answerResponseDto;

    private String title;

    private String contents;

    private int likeCount;

    private int viewCount;
}
