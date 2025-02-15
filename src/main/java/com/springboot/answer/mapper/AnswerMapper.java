package com.springboot.answer.mapper;

import com.springboot.answer.dto.AnswerPatchDto;
import com.springboot.answer.dto.AnswerPostDto;
import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entitiy.Answer;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    AnswerResponseDto answerToAnswerResponseDto(Answer answer);


    default Answer answerPostDtoToAnswer(AnswerPostDto answerPostDto) {
        Question question = new Question();
        question.setQuestionId(answerPostDto.getQuestionId());
        Answer answer = new Answer();
        answer.setContent(answerPostDto.getContent());
        answer.setQuestion(question);

        return answer;
    }

    Answer answerPatchDtoToAnswer(AnswerPatchDto answerPatchDto);


}