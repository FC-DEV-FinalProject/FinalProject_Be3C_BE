package com.be3c.sysmetic.domain.strategy.exception.handler;

import com.be3c.sysmetic.domain.strategy.controller.StrategySearchController;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(basePackageClasses = StrategySearchController.class)
public class StrategySearchExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class})
    protected APIResponse<?> methodArgumentNotValid(MethodArgumentNotValidException e) {
        return APIResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({ NoSuchElementException.class})
    protected APIResponse<?> noSuchElement(NoSuchElementException e) {
        return APIResponse.fail(ErrorCode.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({ Exception.class})
    protected APIResponse<?> exceptionHandler(Exception e) {
        return APIResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage());
    }
}