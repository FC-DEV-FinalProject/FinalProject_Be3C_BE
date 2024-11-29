package com.be3c.sysmetic.domain.member.exception.handler;

import com.be3c.sysmetic.domain.member.exception.MemberBadRequestException;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
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

    // MemberBadRequestException
    @ExceptionHandler(MemberBadRequestException.class)
    public ResponseEntity<APIResponse> handleMemberBadRequestException(MemberBadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
    }

    // Valid 실패 (개별)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<APIResponse> handleValidationException(HandlerMethodValidationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, "유효하지 않은 값입니다.")
        );
    }

    // @Valid 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleValidationException(MethodArgumentNotValidException exception) {
        // 첫 번째 FieldError 추출
        FieldError firstError = exception.getBindingResult().getFieldErrors().get(0);
        String errorMessage = firstError.getDefaultMessage();

        // 첫 번째 에러 메시지 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, errorMessage));
    }

    // @Validated 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<APIResponse> handleBindException(BindException exception) {
        // 첫 번째 FieldError 추출
        FieldError firstError = exception.getBindingResult().getFieldErrors().get(0);
        String errorMessage = firstError.getDefaultMessage();

        // 첫 번째 에러 메시지 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, errorMessage));
//        StringBuilder errorMessages = new StringBuilder();
//        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
//            errorMessages.append(error.getDefaultMessage());
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, errorMessages.toString())
//        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<APIResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.fail(ErrorCode.NOT_FOUND, exception.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, exception.getMessage())
        );
    }



}
