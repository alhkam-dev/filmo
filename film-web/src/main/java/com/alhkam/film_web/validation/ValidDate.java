package com.alhkam.film_web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {

  String message() default "must be a valid date with format dd/MM/yyyy";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
