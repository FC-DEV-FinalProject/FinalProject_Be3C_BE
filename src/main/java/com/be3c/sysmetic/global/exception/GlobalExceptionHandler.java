package com.be3c.sysmetic.global.exception;

import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage() + "글로벌 핸들러"));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<APIResponse<String>> handleValidationExceptions(HandlerMethodValidationException ex) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, ex.getAllValidationResults().get(0).getResolvableErrors().get(0).getDefaultMessage()));
    }
}
