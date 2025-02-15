package com.springboot.question.mapper;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.question.dto.*;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "member.memberId", source = "memberId")
    Question questionPostDtoToQuestion(QuestionPostDto questionPostDto);
    Question questionPatchDtoToQuestion(QuestionPatchDto questionPatchDto);
    default QuestionResponseDto questionToQuestionResponseDto(Question question) {

        QuestionResponseDto questionResponseDto = new QuestionResponseDto();
        questionResponseDto.setQuestionId(question.getQuestionId());
        questionResponseDto.setTitle(question.getTitle());
        questionResponseDto.setContent(question.getContent());
        questionResponseDto.setMemberId(question.getMember().getMemberId());
        questionResponseDto.setNickname(question.getMember().getNickname());
        questionResponseDto.setLikeCount(question.getLikeCount());
        questionResponseDto.setViewCount(question.getViewCount());
        questionResponseDto.setAnswerResponseDto(
                (question.getAnswer() != null) ?
                        new AnswerResponseDto(question.getAnswer().getAnswerId()
                                , question.getAnswer().getContent()) :
                        AnswerResponseDto.defaultMessage()
                );
        return questionResponseDto;
    }

    default List<QuestionResponseDto> questionsToQuestionResponseDto(List<Question> questions) {
        List<QuestionResponseDto> questionResponseDtos = questions.stream()
                .map(question -> questionToQuestionResponseDto(question))
                .collect(Collectors.toList());
        return questionResponseDtos;
    }

}
