package com.be3c.sysmetic.domain.strategy.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DailyDateValidator.class)
public @interface ValidDailyDate {
    String message() default "daily must be today or past";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
