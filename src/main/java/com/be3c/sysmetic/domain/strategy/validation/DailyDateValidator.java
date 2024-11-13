package com.be3c.sysmetic.domain.strategy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyDateValidator implements ConstraintValidator<ValidDailyDate, LocalDateTime> {

    // 오늘 또는 과거 날짜인지 검증
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        LocalDate date = value.toLocalDate();
        return !date.isAfter(LocalDate.now());
    }

}
