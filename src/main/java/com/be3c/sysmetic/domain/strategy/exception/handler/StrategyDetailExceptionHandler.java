package com.be3c.sysmetic.domain.strategy.exception.handler;

import com.be3c.sysmetic.domain.strategy.controller.StrategyDetailController;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(basePackageClasses = StrategyDetailController.class)
public class StrategyDetailExceptionHandler {

    @ExceptionHandler({ NoSuchElementException.class})
    protected APIResponse<?> noSuchElement(NoSuchElementException e) {
        return APIResponse.fail(ErrorCode.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    protected APIResponse<?> nullPointerException(NullPointerException e) {
        return APIResponse.fail(ErrorCode.NOT_FOUND, "전략 상세 페이지가 존재하지 않습니다." + e.getMessage());
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class})
    protected APIResponse<?> methodArgumentNotValid(MethodArgumentNotValidException e) {
        return APIResponse.fail(ErrorCode.BAD_REQUEST, "전략 상세 페이지 요청이 잘못되었습니다.");
    }

    @ExceptionHandler(Exception.class)
    public APIResponse<?> handleAllExceptions(Exception ex) {
        return APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
