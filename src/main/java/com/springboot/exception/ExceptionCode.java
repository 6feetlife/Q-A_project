package com.springboot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    USER_NOT_FOUND(404, "User not found"),
    EXISTING_USER(409, "This user already exists"),
    USER_DORMANT(403, "This account dormant"),
    USER_DEACTIVATED(403,"This account deactivated"),
    NICKNAME_ALREADY_USED(409, "This nickname already used"),
    ANSWER_NOT_FOUND(404, "Answer not found"),
    FORBIDDEN(403, "Access not allowed"),
    QUESTION_NOT_FOUND(404, "Question not found");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.message = message;
        this.status = status;
    }


}
