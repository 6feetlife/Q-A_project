package com.springboot.question.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private String title;

    private String content;

    private String imageUrl = "업로드된 이미지가 없습니다.";

    private AnswerResponseDto answerResponseDto;

    private int likeCount;

    private int viewCount;
}
