package com.springboot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    MEMBER_NOT_FOUND(404, "MEMBER not found"),
    EXISTING_MEMBER(409, "This MEMBER already exists"),
    MEMBER_DORMANT(403, "This account dormant"),
    MEMBER_DEACTIVATED(403,"This account deactivated"),
    NICKNAME_ALREADY_USED(409, "This nickname already used"),
    ANSWER_NOT_FOUND(404, "Answer not found"),
    FORBIDDEN(403, "Access not allowed"),
    QUESTION_NOT_FOUND(404, "Question not found"),
    LIKE_NOT_FOUND(404, "Like not found"),
    UNCHANGABLE_STATE(405, "Method not Allowed");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.message = message;
        this.status = status;
    }


}
