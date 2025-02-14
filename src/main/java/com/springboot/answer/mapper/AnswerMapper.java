package com.springboot.answer.mapper;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entitiy.Answer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    AnswerResponseDto AnswerToAnswerResponseDto(Answer answer);

}
