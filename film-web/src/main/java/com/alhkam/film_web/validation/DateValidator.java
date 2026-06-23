package com.alhkam.film_web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null || value.isBlank()) {
      return true;
    }

    // ResolverStyle.STRICT obliga a Java a validar estrictamente el calendario real
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy").withResolverStyle(ResolverStyle.STRICT);

    try {
      // Intentamos parsear la cadena. Si es un 31/04/2026 lanzará una excepción
      formatter.parse(value);
      return true; // La fecha es real y existe en el calendario
    } catch (DateTimeParseException e) {
      return false; // Fecha imposible o formato incorrecto
    }
  }
}
