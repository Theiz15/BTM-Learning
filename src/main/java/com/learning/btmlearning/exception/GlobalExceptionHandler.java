package com.learning.btmlearning.exception;


import com.learning.btmlearning.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> exception(Exception e) {
        ApiResponse apiResponse = new ApiResponse();
        log.error(e.getMessage());
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getErrorCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getErrorMsg());
        return ResponseEntity.badRequest().body(apiResponse);
    }

//    @ExceptionHandler(RuntimeException.class)
//    ResponseEntity<ApiResponse> runtimeExceptionHandler(RuntimeException e) {
//        ApiResponse apiResponse = new ApiResponse();
//
//        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getErrorCode());
//        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getErrorMsg());
//        return ResponseEntity.badRequest().body(apiResponse);
//    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> appExceptionHandler(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getErrorCode());
        apiResponse.setMessage(errorCode.getErrorMsg());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }


    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> accessDeniedExceptionHandler(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED ;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getErrorCode())
                        .message(errorCode.getErrorMsg())
                        .build()
        );
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    ResponseEntity<ApiResponse> illegalArgumentException(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED ;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getErrorCode())
                        .message(errorCode.getErrorMsg())
                        .build()
        );
    }
}
