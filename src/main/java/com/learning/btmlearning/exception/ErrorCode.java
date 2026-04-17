package com.learning.btmlearning.exception;

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
    ERROR_UPLOAD_FILE(1008,"Error uploading file" , HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(1009,"File not found" , HttpStatus.NOT_FOUND),
    FILE_EMPTY(1010, "File is empty" , HttpStatus.BAD_REQUEST),
    COURSE_NOT_FOUND(1011,"Course not found" , HttpStatus.NOT_FOUND),
    SECTION_NOT_FOUND(1012,"Section not found" , HttpStatus.NOT_FOUND),
    QUIZ_NOT_FOUND(1013,"Quiz not found" , HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND(1014,"Lesson not found" , HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(1015,"Question not found" , HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND(1016,"Answer not found" , HttpStatus.NOT_FOUND),
    INVALID_SINGLE_ANSWER(1017,"There must be exactly one correct answer" , HttpStatus.BAD_REQUEST),
    INVALID_STOCK_QUESTION(1018,"Invalid stock question" , HttpStatus.BAD_REQUEST),
    ENROLLMENT_EXIST(1019,"Enrollment already exists" , HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(1020,"Enrollment not found" , HttpStatus.NOT_FOUND),
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
