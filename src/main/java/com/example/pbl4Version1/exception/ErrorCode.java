package com.example.pbl4Version1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCLASSIFIABLE(9999, "UNKNOWN ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    MATCH_NOT_EXISTED(1012, "Match not existed.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
