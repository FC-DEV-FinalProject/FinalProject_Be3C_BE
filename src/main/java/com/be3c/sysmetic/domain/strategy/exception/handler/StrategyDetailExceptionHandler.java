package com.be3c.sysmetic.domain.strategy.exception.handler;

import com.be3c.sysmetic.domain.strategy.controller.StrategyDetailController;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(basePackageClasses = StrategyDetailController.class)
public class StrategyDetailExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class})
    protected ApiResponse<?> methodArgumentNotValid(MethodArgumentNotValidException e) {
        return ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 전략 상세보기 페이지 요청이 잘못되었습니다.");
    }

    @ExceptionHandler({ NoSuchElementException.class})
    protected ApiResponse<?> noSuchElement(NoSuchElementException e) {
        return ApiResponse.fail(ErrorCode.NOT_FOUND, e.getMessage());
    }
}
