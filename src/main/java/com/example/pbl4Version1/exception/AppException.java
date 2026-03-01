package com.example.pbl4Version1.exception;

public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }

    public AppException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.name() : "APP_ERROR");
    }
}
