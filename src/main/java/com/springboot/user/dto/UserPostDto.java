package com.springboot.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class UserPostDto {

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,16}$", message = "주민등록상 이름을 입력하여주세요")
    private String userName;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-z]{2,8}$", message = "한글과 영어 소문자만 사용 가능합니다.")
    private String userNickname;

    @NotBlank
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "010-XXXX-XXXX 형식이어야 합니다.")
    private String phone;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "비밀번호는 8~20자이며, 최소 하나의 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

}
