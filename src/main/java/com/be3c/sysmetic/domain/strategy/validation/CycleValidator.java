package com.be3c.sysmetic.domain.strategy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CycleValidator implements ConstraintValidator<ValidCycle, Character> {

    // 주기 D 또는 P가 아닐 경우 exception
    @Override
    public boolean isValid(Character value, ConstraintValidatorContext context) {
        return value == 'D' || value == 'P';
    }

}