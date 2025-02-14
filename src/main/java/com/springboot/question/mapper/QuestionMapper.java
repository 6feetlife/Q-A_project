package com.springboot.question.mapper;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.question.dto.QuestionPatchDto;
import com.springboot.question.dto.QuestionPostDto;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.dto.QuestionsResponseDto;
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
        QuestionResponseDto questionResponseDto =
                new QuestionResponseDto(
                        question.getQuestionId(),
                        question.getMember().getMemberId(),
                        question.getMember().getNickname(),
                        new AnswerResponseDto(
                                question.getAnswer().getAnswerId(),
                                question.getAnswer().getContent()
                                ),
                        question.getTitle(),
                        question.getContent(),
                        question.getLikeCount(),
                        question.getViewCount()
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
