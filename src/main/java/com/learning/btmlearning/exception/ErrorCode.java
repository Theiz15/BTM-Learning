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
    CATEGORY_NOT_FOUND(1008, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTS(1009, "Category name already exists", HttpStatus.BAD_REQUEST),
    COURSE_NOT_FOUND(1010, "Course not found", HttpStatus.NOT_FOUND),
    CERTIFICATE_ALREADY_ISSUED(1011, "Certificate already issued for this course", HttpStatus.BAD_REQUEST),
    CERTIFICATE_ISSUE_CONDITION_NOT_MET(1012, "Certificate issue conditions are not met", HttpStatus.BAD_REQUEST),
    CERTIFICATE_NOT_FOUND(1013, "Certificate not found", HttpStatus.NOT_FOUND),
    COURSE_REVIEW_ALREADY_EXISTS(1014, "User already reviewed this course", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND(1015, "Notification not found", HttpStatus.NOT_FOUND),

    CLOUDINARY_NOT_CONFIGURED(1016, "Cloudinary is not configured", HttpStatus.INTERNAL_SERVER_ERROR),
    CLOUDINARY_UPLOAD_FAILED(1017, "Cloudinary upload failed", HttpStatus.BAD_REQUEST),
    THUMBNAIL_INVALID_MIME_TYPE(1018, "Thumbnail file type is not supported", HttpStatus.BAD_REQUEST),
    THUMBNAIL_FILE_TOO_LARGE(1019, "Thumbnail file is too large", HttpStatus.BAD_REQUEST),
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
