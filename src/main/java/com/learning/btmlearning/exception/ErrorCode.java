package com.learning.btmlearning.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Uncategorized error" , HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User already existed" , HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1010,"Email Already Exists" , HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least 3 characters" , HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004,"password must be at least 8 characters" , HttpStatus.BAD_REQUEST),
    // INVALID_MESSAGE_KEY(1005,"invalid message key"),
    USER_NOT_EXISTED(1005,"user not existed" , HttpStatus.NOT_FOUND),
    CANNOT_CHANGE_PASSWORD(1008,"Cannot change password" , HttpStatus.NOT_FOUND),
    PASSWORD_NOT_MERGE(1009,"your password not merge" , HttpStatus.NOT_FOUND),
    CANNOT_UPLOAD_IMAGE(1008,"Cannot upload image" , HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Unauthenticated" , HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"You do not have permission" , HttpStatus.FORBIDDEN),


    SLUG_REALLY_EXIST(1008,"slug already exist" , HttpStatus.UNAUTHORIZED),
    CATEGORY_NOT_FOUND(1009,"Category not found" , HttpStatus.UNAUTHORIZED),
    COURSE_NOT_FOUND(1010,"Course not found" , HttpStatus.UNAUTHORIZED),
    YOU_ARE_NOT_INSTRUCTOR(1011,"You are not instructor" , HttpStatus.UNAUTHORIZED),
    YOU_ARE_OWN(1012,"You already own this course." , HttpStatus.UNAUTHORIZED),
    AI_SERVER_OVERLOADED(1013,"Ai server overloaded" , HttpStatus.UNAUTHORIZED),
    TOO_MANY_REQUESTS(1014,"Too many request" , HttpStatus.UNAUTHORIZED),
    CANNOT_CHANGE_ROLE(1015,"You cannot change role yourself" , HttpStatus.UNAUTHORIZED),
    EMAIL_IN_USE(1016,"Your email is really in use" , HttpStatus.UNAUTHORIZED)
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
