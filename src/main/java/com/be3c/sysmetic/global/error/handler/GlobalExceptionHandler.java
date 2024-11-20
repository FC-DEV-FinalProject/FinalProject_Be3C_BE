package com.be3c.sysmetic.global.error.handler;

import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<APIResponse<String>> handleValidationExceptions(HandlerMethodValidationException ex) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, ex.getAllValidationResults().get(0).getResolvableErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler({UsernameNotFoundException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<APIResponse<String>> handleAuthenticationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(APIResponse.fail(ErrorCode.FORBIDDEN, ex.getMessage()));
    }

}
