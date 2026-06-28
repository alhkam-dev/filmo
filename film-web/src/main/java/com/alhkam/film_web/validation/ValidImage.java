package com.alhkam.film_web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

  String message() default "Solo se permiten imágenes válidas (JPEG, PNG, WEBP)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
