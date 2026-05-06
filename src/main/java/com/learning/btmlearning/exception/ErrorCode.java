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
    USER_NOT_EXISTED(1006,"user not existed" , HttpStatus.NOT_FOUND),
    CANNOT_CHANGE_PASSWORD(1007,"Cannot change password" , HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MERGE(1008,"your password not merge" , HttpStatus.BAD_REQUEST),
    CANNOT_UPLOAD_IMAGE(1009,"Cannot upload image" , HttpStatus.INTERNAL_SERVER_ERROR),
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
    SLUG_REALLY_EXIST(1025,"slug already exist" , HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND(1026,"Category not found" , HttpStatus.NOT_FOUND),
    YOU_ARE_NOT_INSTRUCTOR(1027,"You are not instructor" , HttpStatus.FORBIDDEN),
    YOU_ARE_OWN(1028,"You already own this course." , HttpStatus.BAD_REQUEST),
    AI_SERVER_OVERLOADED(1029,"Ai server overloaded" , HttpStatus.SERVICE_UNAVAILABLE),
    TOO_MANY_REQUESTS(1030,"Too many requests" , HttpStatus.TOO_MANY_REQUESTS),
    CANNOT_CHANGE_ROLE(1031,"You cannot change role yourself" , HttpStatus.FORBIDDEN),
    VOUCHER_NOT_FOUND(1032,"Voucher is not found" , HttpStatus.NOT_FOUND),
    CODE_ALREADY_EXIST(1033,"Voucher code already exists" , HttpStatus.CONFLICT),
    USER_ALREADY_INSTRUCTOR(1034, "User is already instructor", HttpStatus.BAD_REQUEST),
    COURSE_REVIEW_REQUIRES_COMPLETION(1035, "Only users who completed this course can review", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_EXISTS(1036, "Category name already exists", HttpStatus.CONFLICT),
    CERTIFICATE_ALREADY_ISSUED(1037, "Certificate already issued for this course", HttpStatus.CONFLICT),
    CERTIFICATE_ISSUE_CONDITION_NOT_MET(1038, "Certificate issue conditions are not met", HttpStatus.BAD_REQUEST),
    CERTIFICATE_NOT_FOUND(1039, "Certificate not found", HttpStatus.NOT_FOUND),
    COURSE_REVIEW_ALREADY_EXISTS(1040, "User already reviewed this course", HttpStatus.CONFLICT),
    NOTIFICATION_NOT_FOUND(1041, "Notification not found", HttpStatus.NOT_FOUND),
    CLOUDINARY_NOT_CONFIGURED(1042, "Cloudinary is not configured", HttpStatus.INTERNAL_SERVER_ERROR),
    CLOUDINARY_UPLOAD_FAILED(1043, "Cloudinary upload failed", HttpStatus.BAD_REQUEST),
    THUMBNAIL_INVALID_MIME_TYPE(1044, "Thumbnail file type is not supported", HttpStatus.BAD_REQUEST),
    THUMBNAIL_FILE_TOO_LARGE(1045, "Thumbnail file is too large", HttpStatus.BAD_REQUEST),
    INVALID_FILE_NAME(1046, "File name is invalid", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1047, "File type is invalid", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(1048, "File too large", HttpStatus.BAD_REQUEST),
    COURSE_NOT_PUBLISHED(1049, "Course is not published", HttpStatus.BAD_REQUEST),
    COURSE_NOT_FREE(1050, "This course is not free. Please use VNPay gateway.", HttpStatus.BAD_REQUEST),
    ENROLLMENT_ALREADY_COMPLETED(1051, "Enrollment is already completed", HttpStatus.BAD_REQUEST),
    ENROLLMENT_ALREADY_CANCELLED(1052, "Enrollment is already cancelled", HttpStatus.BAD_REQUEST),
    ENROLLMENT_CANNOT_REACTIVATE(1053, "Can only reactivate cancelled enrollment", HttpStatus.BAD_REQUEST),
    COURSE_NOT_PENDING(1054, "Course is not in PENDING status", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND(1055, "Payment not found", HttpStatus.NOT_FOUND),
    USER_NOT_ENROLLED(1056, "User is not enrolled in this course", HttpStatus.BAD_REQUEST);

    ErrorCode(int errorCode, String errorMsg, HttpStatusCode statusCode) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.statusCode = statusCode;
    }

    private final int errorCode;
    private final String errorMsg;
    private final HttpStatusCode statusCode;
}
