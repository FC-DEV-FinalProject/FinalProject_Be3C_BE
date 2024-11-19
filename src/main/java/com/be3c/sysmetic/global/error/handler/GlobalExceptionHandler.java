package com.be3c.sysmetic.global.error.handler;

import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(HandlerMethodValidationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(ErrorCode.BAD_REQUEST, ex.getAllValidationResults().get(0).getResolvableErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(ErrorCode.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

}
