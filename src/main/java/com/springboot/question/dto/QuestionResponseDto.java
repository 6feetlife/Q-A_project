package com.springboot.question.dto;

import com.springboot.answer.dto.AnswerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDto {

    private Long questionId;

    private Long memberId;

    private String nickname;

    private AnswerResponseDto answerResponseDto;

    private String title;

    private String content;

    private int likeCount;

    private int viewCount;
}
