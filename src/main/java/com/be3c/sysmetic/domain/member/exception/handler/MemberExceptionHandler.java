package com.be3c.sysmetic.domain.member.exception.handler;

import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

// member package
@RestControllerAdvice(basePackages = {"com.be3c.sysmetic.domain.member"})
public class MemberExceptionHandler {

    // Valid 실패 (개별)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<APIResponse> handleValidationException(HandlerMethodValidationException exception) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, "유효하지 않은 값입니다."));
    }

    // @Valid validation failures (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleValidationException(MethodArgumentNotValidException exception) {
        StringBuilder errorMessages = new StringBuilder();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorMessages.append(error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                APIResponse.fail(ErrorCode.BAD_REQUEST, errorMessages.toString())
        );
    }

    // @Validated validation failures (BindException or others)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<APIResponse> handleBindException(BindException exception) {
        StringBuilder errorMessages = new StringBuilder();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorMessages.append(error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                APIResponse.fail(ErrorCode.BAD_REQUEST, errorMessages.toString())
        );
    }


}
