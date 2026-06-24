package com.alhkam.film_web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null || value.isBlank()) {
      return true;
    }

    // ResolverStyle.STRICT obliga a Java a validar estrictamente el calendario real.
    // Usar uuuu que significa año de la era actual. yyyy necesita que indice si antes o despues de
    // cristo al ser STRICT y también utilizar IsoChronology.INSTANCE
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withChronology(IsoChronology.INSTANCE)
            .withResolverStyle(ResolverStyle.STRICT);

    try {
      LocalDate.parse(value, formatter);
      return true;
    } catch (DateTimeParseException e) {
      System.out.println("DEBUG VALIDATOR - Error parseando: '" + value + "' -> " + e.getMessage());
      return false;
    }
  }
}
