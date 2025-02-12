package com.springboot.user.dto;

import com.springboot.question.dto.QuestionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {

    private String userNickname;

    private String userName;

    private String phone;

    private String email;

    private List<QuestionResponseDto> questionResponseDtos;


}
