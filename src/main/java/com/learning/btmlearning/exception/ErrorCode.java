package com.learning.btmlearning.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Uncategorized error" , HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User already existed" , HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least 3 characters" , HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004,"password must be at least 8 characters" , HttpStatus.BAD_REQUEST),
    // INVALID_MESSAGE_KEY(1005,"invalid message key"),
    USER_NOT_EXISTED(1005,"user not existed" , HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Unauthenticated" , HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"You do not have permission" , HttpStatus.FORBIDDEN),
    ;

    ErrorCode(int errorCode, String errorMsg, HttpStatusCode statusCode) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.statusCode = statusCode;
    }

    private final int errorCode;
    private final String errorMsg;
    private final HttpStatusCode statusCode;
}
