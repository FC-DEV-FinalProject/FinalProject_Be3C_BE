package com.be3c.sysmetic.domain.strategy.validation;

import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyDateValidator implements ConstraintValidator<ValidDailyDate, LocalDate> {

    // 오늘 또는 과거 날짜인지 검증
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate date = value;
        boolean isValid = !date.isAfter(LocalDate.now());
        if(!isValid) {
            throw new StrategyBadRequestException(StrategyExceptionMessage.INVALID_DATE.getMessage(), ErrorCode.BAD_REQUEST);
        }
        return isValid;
    }
}
