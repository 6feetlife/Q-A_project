package com.springboot.question.dto;

import com.springboot.answer.dto.AnswerResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionsResponseDto {

    private Long questionId;

    private String userNickname;

    private String answerContent;

    private String title;

    private String content;

    private int likeCount;

    private int viewCount;
}
