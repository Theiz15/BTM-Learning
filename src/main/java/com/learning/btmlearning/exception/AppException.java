package com.learning.btmlearning.exception;

import lombok.Getter;

public class AppException extends RuntimeException {
    @Getter
    private ErrorCode errorCode;

    public AppException( ErrorCode errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode;
    }
}
