package com.learning.btmlearning.exception;

import lombok.Getter;

public class AppException extends RuntimeException {
    @Getter
    private ErrorCode errorCode;
    private String message;
    public AppException( ErrorCode errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode;
    }
}
