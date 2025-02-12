package com.springboot.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    USER_NOT_FOUND(404, "User not found"),
    EXISTING_USER(409, "This user already exists"),
    ANSWER_NOT_FOUND(404, "Answer not found"),
    FORBIDDEN(403, "Access not allowed"),
    QUESTION_NOT_FOUND(404, "Question not found");

    @Getter
    private int errorCode;

    @Getter
    private String message;

    ExceptionCode(int errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }


}
