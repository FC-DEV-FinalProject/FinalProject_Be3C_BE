package com.be3c.sysmetic.domain.strategy.exception.handler;

import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// strategy package
@RestControllerAdvice(basePackages = {"com.be3c.sysmetic.domain.strategy"})
public class StrategyExceptionHandler {

    // custom exception
    @ExceptionHandler(StrategyBadRequestException.class)
    public ResponseEntity<APIResponse> badRequest(StrategyBadRequestException exception) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, exception.getMessage()));
    }

    // valid 검증 실패 exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> badRequest(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, StrategyExceptionMessage.INVALID_VALUE.getMessage()));
    }

    // 필수 parameter 미입력 exception
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse> badRequest(MissingServletRequestParameterException exception) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, StrategyExceptionMessage.INVALID_PARAMETER.getMessage()));
    }
}