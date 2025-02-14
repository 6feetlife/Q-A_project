package com.springboot.member.dto;

import com.springboot.question.dto.QuestionResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDto {

    private String nickname;

    private String name;

    private String phone;

    private String email;

    private List<QuestionResponseDto> questionResponseDtos;

}
