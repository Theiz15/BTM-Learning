package com.learning.btmlearning.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Uncategorized error" , HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User already existed" , HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1003,"Email Already Exists" , HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004,"Username must be at least 3 characters" , HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005,"password must be at least 8 characters" , HttpStatus.BAD_REQUEST),
    // INVALID_MESSAGE_KEY(1005,"invalid message key"),
    USER_NOT_EXISTED(1006,"user not existed" , HttpStatus.NOT_FOUND),
    CANNOT_CHANGE_PASSWORD(1007,"Cannot change password" , HttpStatus.NOT_FOUND),
    PASSWORD_NOT_MERGE(1008,"your password not merge" , HttpStatus.NOT_FOUND),
    CANNOT_UPLOAD_IMAGE(1009,"Cannot upload image" , HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1010,"Unauthenticated" , HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1011,"You do not have permission" , HttpStatus.FORBIDDEN),
    ERROR_UPLOAD_FILE(1012,"Error uploading file" , HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(1013,"File not found" , HttpStatus.NOT_FOUND),
    FILE_EMPTY(1014, "File is empty" , HttpStatus.BAD_REQUEST),
    COURSE_NOT_FOUND(1015,"Course not found" , HttpStatus.NOT_FOUND),
    SECTION_NOT_FOUND(1016,"Section not found" , HttpStatus.NOT_FOUND),
    QUIZ_NOT_FOUND(1017,"Quiz not found" , HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND(1018,"Lesson not found" , HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(1019,"Question not found" , HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND(1020,"Answer not found" , HttpStatus.NOT_FOUND),
    INVALID_SINGLE_ANSWER(1021,"There must be exactly one correct answer" , HttpStatus.BAD_REQUEST),
    INVALID_STOCK_QUESTION(1022,"Invalid stock question" , HttpStatus.BAD_REQUEST),
    ENROLLMENT_EXIST(1023,"Enrollment already exists" , HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(1024,"Enrollment not found" , HttpStatus.NOT_FOUND),


    SLUG_REALLY_EXIST(1025,"slug already exist" , HttpStatus.UNAUTHORIZED),
    CATEGORY_NOT_FOUND(1026,"Category not found" , HttpStatus.UNAUTHORIZED),
    YOU_ARE_NOT_INSTRUCTOR(1027,"You are not instructor" , HttpStatus.UNAUTHORIZED),
    YOU_ARE_OWN(1028,"You already own this course." , HttpStatus.UNAUTHORIZED),
    AI_SERVER_OVERLOADED(1029,"Ai server overloaded" , HttpStatus.UNAUTHORIZED),
    TOO_MANY_REQUESTS(1030,"Too many request" , HttpStatus.UNAUTHORIZED),
    CANNOT_CHANGE_ROLE(1031,"You cannot change role yourself" , HttpStatus.UNAUTHORIZED) ,
    VOUCHER_NOT_FOUND(1032,"Voucher is not found" , HttpStatus.NOT_FOUND),
    CODE_ALREADY_EXIST(1033,"Voucher code is ready exist" , HttpStatus.NOT_FOUND)
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
