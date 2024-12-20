package com.be3c.sysmetic.domain.strategy.exception.handler;

import com.be3c.sysmetic.domain.strategy.controller.StrategyController;
import com.be3c.sysmetic.domain.strategy.controller.TraderStrategyController;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// strategy package
@RestControllerAdvice(assignableTypes = {StrategyController.class, TraderStrategyController.class})
@Order(1)
public class StrategyExceptionHandler {

    // custom exception
    @ExceptionHandler(StrategyBadRequestException.class)
    public ResponseEntity<APIResponse> badRequest(StrategyBadRequestException exception) {
        return ResponseEntity.status(exception.getStatus()).body(APIResponse.fail(exception.getErrorCode(), exception.getMessage()));
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

    // entity not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<APIResponse> badRequest(EntityNotFoundException exception) {
        return ResponseEntity.badRequest().body(APIResponse.fail(ErrorCode.BAD_REQUEST, StrategyExceptionMessage.DATA_NOT_FOUND.getMessage()));
    }

}