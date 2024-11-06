package com.be3c.sysmetic.domain.strategy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CycleValidator implements ConstraintValidator<ValidCycle, Character> {

    @Override
    public boolean isValid(Character value, ConstraintValidatorContext context) {
        return value == 'D' || value == 'P';
    }

}
