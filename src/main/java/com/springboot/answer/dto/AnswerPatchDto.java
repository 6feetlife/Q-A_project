package com.springboot.answer.dto;

import com.springboot.answer.entitiy.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class AnswerPatchDto {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z가-힣!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?`~ ]{1,800}$")
    private String content;
}
